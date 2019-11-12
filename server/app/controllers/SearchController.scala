package controllers

import java.io.File
import java.lang.reflect.Field
import java.nio.file.Files

import javax.inject.Inject
import play.api.mvc._
import dao._
import models.Tables._
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.math3.stat.StatUtils
import play.api.cache.AsyncCacheApi
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data.Form
import play.api.data.Forms._
import tool.Pojo.{GeneGoJson, GeneGoSeq}
import tool.{FormTool, Tool}
import utils.Utils.KeywordInfo
import utils.{ExecCommand, Utils}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.xml._
import scala.collection.JavaConverters._
import scala.collection.mutable
import utils.Implicits._

case class RegionData(chr: String, start: Int, end: Int)

case class PageData(limit: Int, offset: Int, order: String, search: Option[String], sort: Option[String])

class SearchController @Inject()(cc: ControllerComponents, fpkmMessageDao: FpkmMessageDao, fpkmDao: FpkmDao,
                                 allInfoDao: AllInfoDao, formTool: FormTool, tool: Tool, basicDao: BasicDao,
                                 geneGoDao: GeneGoDao, geneKeggDao: GeneKeggDao, orthologsDao: OrthologsDao,
                                 cache: AsyncCacheApi, otherOrthologsDao: OtherOrthologsDao, plagueListDao: PlagueListDao,
                                 hiberListDao: HiberListDao
                                ) extends AbstractController(cc) {


  val regionForm = Form(
    mapping(
      "chr" -> text,
      "start" -> number,
      "end" -> number
    )(RegionData.apply)(RegionData.unapply)
  )

  case class RegionStrData(region: String)

  val regionStrForm = Form(
    mapping(
      "region" -> text
    )(RegionStrData.apply)(RegionStrData.unapply)
  )

  def searchByGeneIdBefore = Action { implicit request =>
    Ok(views.html.search.searchByGeneId(None))
  }

  def searchBefore = Action { implicit request =>
    Ok(views.html.search.search(None))
  }

  def searchBefore1 = Action { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    val geneId = if (StringUtils.isBlank(data.geneId)) "Mar00001" else data.geneId
    Ok(views.html.search.search(Some(geneId)))
  }

  def searchByGeneNameBefore = Action { implicit request =>
    Ok(views.html.search.searchByGeneName())
  }

  def getAllGeneNames = Action.async {
    allInfoDao.selectAllGeneNames.map { x =>
      val geneNames = x.distinct
      Ok(Json.toJson(x))
    }
  }

  def getAllGeneGo = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    geneGoDao.selectByGeneId(data.geneId).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def getAllGeneKegg = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    geneKeggDao.selectByGeneId(data.geneId).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def getAllOrthologs = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    orthologsDao.selectByGeneIdO(data.geneId).zip(otherOrthologsDao.selectByGeneIdO(data.geneId)).map { case (x, other) =>
      val map = x.map { y =>
        Utils.getMapByT(y)
      }.getOrElse(Map[String, String]())
      val otherMap = other.map { y =>
        Utils.getMapByT(y)
      }.getOrElse(Map[String, String]())
      val finalMap = map ++ otherMap
      Ok(Json.toJson(finalMap))
    }
  }

  def getDegInfo = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    plagueListDao.selectByGeneIdO(data.geneId).zip(hiberListDao.selectByGeneIdO(data.geneId)).map { case (x, hiber) =>
      val plagueRs = x.map { y =>
        "yes"
      }.getOrElse("no")
      val hiberRs = hiber.map { y =>
        "yes"
      }.getOrElse("no")
      val finalMap = Map("plague" -> plagueRs, "hiber" -> hiberRs)
      Ok(Json.toJson(finalMap))
    }
  }

  def getAllGeneIds = Action.async {
    allInfoDao.selectAllGeneIds.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllS2GeneIds = Action.async {
    allInfoDao.selectAllGeneIds.map { x =>
      val jsons = x.zipWithIndex.map { case (v, i) =>
        Json.obj("id" -> i, "text" -> v)
      }
      Ok(Json.toJson(jsons))
    }
  }

  def searchByGeneId = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest.get
    val species = formTool.speciesForm.bindFromRequest().get.species
    val geneIds = data.geneId.split(",").distinct
    basicDao.selectByGeneIds(geneIds, species).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def searchByKeyword = Action.async { implicit request =>
    val data = formTool.keywordForm.bindFromRequest.get
    val keywords = data.keyword.trim.split("\\s+").toBuffer.distinct
    cache.getOrElseUpdate[Seq[BasicRow]]("basics")(basicDao.selectAll).flatMap { case x =>
      val keywordsMap = x.map { y =>
        (y, KeywordInfo(keywords, Map[String, String]()))
      }.toMap
      val searchMap = Utils.getSearchMap(keywordsMap, "id", Seq("id", "symbol", "description"))
      val ids = searchMap.keySet.toBuffer
      cache.getOrElseUpdate[Seq[GeneGoSeq]]("geneGoSeqs")(tool.getGeneGoSeqs(ids)).map { x =>
        x.map(y => (y, searchMap(y.geneId))).toMap
      }
    }.flatMap { infos =>
      val searchMap = Utils.getGeneGoSearchMap(infos)
      val ids = searchMap.keySet.toBuffer
      cache.getOrElseUpdate[Seq[GeneKeggRow]]("geneKeggs")(geneKeggDao.selectAll(ids)).map { x =>
        x.map(y => (y, searchMap(y.geneId))).toMap
      }
    }.map { infos =>
      val searchMap = Utils.getSearchMap(infos, "geneId", Seq("description"))
      val array = searchMap.filter(x => x._2.remainKeywords.size == 0).map { case (geneId, keywordInfo) =>
        Json.obj("id" -> geneId, "match" -> Json.toJson(keywordInfo.map))
      }
      val json = Json.obj("columnNames" -> ArrayBuffer("id", "match"), "array" -> array)
      Ok(json)
    }

  }

  case class GeneNameData(geneName: String)

  val geneNameForm = Form(
    mapping(
      "geneName" -> text
    )(GeneNameData.apply)(GeneNameData.unapply)
  )

  def searchByGeneName = Action.async { implicit request =>
    val data = geneNameForm.bindFromRequest.get
    val species = formTool.speciesForm.bindFromRequest().get.species
    val geneNames = data.geneName.split(",").distinct
    basicDao.selectByGeneNames(geneNames, species).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def searchByRegionBefore = Action { implicit request =>
    Ok(views.html.search.searchByRegion())
  }


  def getDetailInfo = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    basicDao.selectByGeneId(data.geneId).map { x =>
      Ok(views.html.search.detailInfo(x))
    }
  }

  def getBasicInfo = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    basicDao.selectBasicByGeneId(data.geneId).map { x =>
      val array = Utils.getMapByT(x)
      Ok(Json.toJson(array))
    }
  }

  def getAllChrs = Action.async { implicit request =>
    val species = formTool.speciesForm.bindFromRequest().get.species
    basicDao.selectAllChrs(species).map { x =>
      Ok(Json.toJson(x))
    }
  }

  def searchByRegion = Action.async { implicit request =>
    val data = regionForm.bindFromRequest.get
    val species = formTool.speciesForm.bindFromRequest().get.species
    basicDao.selectByRegionData(data).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def browseBefore = Action { implicit request =>
    Ok(views.html.search.browse())
  }

  def seqFetchBefore = Action { implicit request =>
    Ok(views.html.search.seqFetch())
  }

  def blastn = Action(parse.multipartFormData) { implicit request =>
    val data = formTool.blastForm.bindFromRequest.get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val seqFile = new File(tmpDir, "seq.fasta")
    val method = "blastn"
    data.queryText.map { queryText =>
      FileUtils.writeStringToFile(seqFile, queryText)
    }.getOrElse {
      val file = request.body.file("file").get
      file.ref.moveTo(seqFile, replace = true)
    }
    val outXmlFile = new File(tmpDir, "data.xml")
    val outHtmlFile = new File(tmpDir, "data.html")
    val blastFile = new File(Utils.blastBinFile, method)
    val dbFile = Tool.getDbFile(data.againstType)
    val command =
      s"""
         |${blastFile.unixPath} -query ${seqFile.unixPath} -db ${dbFile.unixPath} -outfmt 5 -evalue ${data.evalue}  -max_target_seqs ${data.maxTargetSeqs} -word_size ${data.wordSize} -out ${outXmlFile.unixPath}
         |python ${Utils.blast2HtmlFile.unixPath} -i ${outXmlFile.unixPath} -o ${outHtmlFile.unixPath}
         |""".stripMargin
    val execCommand = Utils.callLinuxScript(tmpDir, ArrayBuffer(command))
    if (execCommand.isSuccess) {
      val html = FileUtils.readFileToString(outHtmlFile)
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("html" -> (html + "\n" + Utils.scriptHtml)))
    } else {
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def blastp = Action(parse.multipartFormData) { implicit request =>
    val method = "blastp"
    val data = formTool.blastForm.bindFromRequest.get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val seqFile = new File(tmpDir, "seq.fasta")
    data.queryText.map { queryText =>
      FileUtils.writeStringToFile(seqFile, queryText)
    }.getOrElse {
      val file = request.body.file("file").get
      file.ref.moveTo(seqFile, replace = true)
    }
    val outXmlFile = new File(tmpDir, "data.xml")
    val outHtmlFile = new File(tmpDir, "data.html")
    val blastFile = new File(Utils.blastBinFile, method)
    val dbFile = Tool.getProteinDbFile(data.againstType)
    val command =
      s"""
         |${blastFile.unixPath} -query ${seqFile.unixPath} -db ${dbFile.unixPath} -outfmt 5 -evalue ${data.evalue}  -max_target_seqs ${data.maxTargetSeqs} -word_size ${data.wordSize} -out ${outXmlFile.unixPath}
         |python ${Utils.blast2HtmlBinFile.unixPath}/blastp2html.py -i ${outXmlFile.unixPath} -o ${outHtmlFile.unixPath} --template ${Utils.blast2HtmlBinFile.unixPath}/blastp.jinja
         |""".stripMargin
    val execCommand = Utils.callLinuxScript(tmpDir, shBuffer = ArrayBuffer(command))
    if (execCommand.isSuccess) {
      val html = FileUtils.readFileToString(outHtmlFile)
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("html" -> (html + "\n" + Utils.scriptHtml)))
    } else {
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def blastx = Action(parse.multipartFormData) { implicit request =>
    val method = "blastx"
    val data = formTool.blastForm.bindFromRequest.get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val seqFile = new File(tmpDir, "seq.fasta")
    data.queryText.map { queryText =>
      FileUtils.writeStringToFile(seqFile, queryText)
    }.getOrElse {
      val file = request.body.file("file").get
      file.ref.moveTo(seqFile, replace = true)
    }
    val outXmlFile = new File(tmpDir, "data.xml")
    val outHtmlFile = new File(tmpDir, "data.html")
    val blastFile = new File(Utils.blastBinFile, method)
    val dbFile = Tool.getProteinDbFile(data.againstType)
    val command =
      s"""
         |${blastFile.unixPath} -query ${seqFile.unixPath} -db ${dbFile.unixPath} -outfmt 5 -evalue ${data.evalue}  -max_target_seqs ${data.maxTargetSeqs} -word_size ${data.wordSize} -out ${outXmlFile.unixPath}
         |python ${Utils.blast2HtmlBinFile.unixPath}/${method}2html.py -i ${outXmlFile.unixPath} -o ${outHtmlFile.unixPath} --template ${Utils.blast2HtmlBinFile.unixPath}/${method}.jinja
         |""".stripMargin
    val execCommand = Utils.callLinuxScript(tmpDir, shBuffer = ArrayBuffer(command))
    if (execCommand.isSuccess) {
      val html = FileUtils.readFileToString(outHtmlFile)
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("html" -> (html + "\n" + Utils.scriptHtml)))
    } else {
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def tblastn = Action(parse.multipartFormData) { implicit request =>
    val method = "tblastn"
    val data = formTool.blastForm.bindFromRequest.get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val seqFile = new File(tmpDir, "seq.fasta")
    data.queryText.map { queryText =>
      FileUtils.writeStringToFile(seqFile, queryText)
    }.getOrElse {
      val file = request.body.file("file").get
      file.ref.moveTo(seqFile, replace = true)
    }
    val outXmlFile = new File(tmpDir, "data.xml")
    val outHtmlFile = new File(tmpDir, "data.html")
    val blastFile = new File(Utils.blastBinFile, method)
    val dbFile = Tool.getDbFile(data.againstType)
    val command1 = blastFile + " -query " + seqFile.getAbsolutePath + " -db " + dbFile +
      " -outfmt 5 -evalue " + data.evalue + " -max_target_seqs " + data.maxTargetSeqs + " -word_size " +
      data.wordSize + " -out " + outXmlFile.getAbsolutePath
    val command2 = "python " + s"${Utils.blast2HtmlBinFile}/${method}2html.py" + " -i " + outXmlFile + " -o " + outHtmlFile + s" --template ${new File(Utils.blast2HtmlBinFile, method + ".jinja")}"
    val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(command1, command2))
    if (execCommand.isSuccess) {
      val html = FileUtils.readFileToString(outHtmlFile)
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("html" -> (html + "\n" + Utils.scriptHtml)))
    } else {
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def blastResult = Action { implicit request =>
    val file = new File("E:\\testData\\data.xml")
    val xml = scala.xml.XML.loadFile(file)
    Ok(views.html.search.blastResult(xml))
  }

  def blastBefore = Action { implicit request =>
    Ok(views.html.search.blast())
  }

  val pageForm = Form(
    mapping(
      "limit" -> number,
      "offset" -> number,
      "order" -> text,
      "search" -> optional(text),
      "sort" -> optional(text)
    )(PageData.apply)(PageData.unapply)
  )

  def getAllAnnos = Action { implicit request =>
    val page = pageForm.bindFromRequest.get
    if (Utils.data.isEmpty) {
      Utils.data = Await.result(allInfoDao.selectAll, Duration.Inf)
    }
    val x = Utils.data
    val orderX = Utils.dealDataByPage(x, page)
    val total = orderX.size
    val tmpX = orderX.slice(page.offset, page.offset + page.limit)
    val array = getArrayByAllInfos(tmpX)
    Ok(Json.obj("rows" -> array, "total" -> total))
  }


  def seqMultipleQueryBefore = Action { implicit request =>
    Ok(views.html.search.seqMultipleQuery())
  }

  def seqMultipleQuery = Action { implicit request =>
    val data = regionStrForm.bindFromRequest.get
    val tmpDir = Files.createTempDirectory("tmpDir").toFile
    val outFile = new File(tmpDir, "data.txt")
    val queryStr = data.region
    val execCommand = new ExecCommand
    val command = Utils.samtoolsPath + " faidx " + new File(Utils.seqsDir, "Marmot.masked.genome.fasta") + " " + queryStr
    execCommand.exec(command, outFile, tmpDir)
    if (execCommand.isSuccess) {
      val dataStr = FileUtils.readFileToString(outFile)
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("data" -> dataStr))
    } else {
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }


  def seqQuery = Action { implicit request =>
    val data = regionForm.bindFromRequest.get
    val tmpDir = Files.createTempDirectory("tmpDir").toFile
    val outFile = new File(tmpDir, "data.txt")
    val queryStr = data.chr + ":" + data.start + "-" + data.end
    val execCommand = new ExecCommand
    val command = Utils.samtoolsPath + " faidx " + new File(Utils.seqsDir, "Marmot.masked.genome.fasta") + " " + queryStr
    execCommand.exec(command, outFile, tmpDir)
    if (execCommand.isSuccess) {
      val dataStr = FileUtils.readFileToString(outFile)
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("data" -> dataStr))
    } else {
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def seqQueryBefore = Action { implicit request =>
    Ok(views.html.search.seqQuery())
  }

  def boxPlotByGeneId = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    val fpkms = fpkmDao.selectByGeneId(data.geneId)
    fpkms.map(x => Ok {
      val typeX = x.groupBy(_.group)
      val sortX = typeX
      val typeLong = sortX.map {
        case (kind, longs) =>
          val values = longs.map(_.value).toArray
          val minValue = values.min
          val Q1 = StatUtils.percentile(values, 25)
          val Q2 = StatUtils.percentile(values, 50)
          val Q3 = StatUtils.percentile(values, 75)
          val maxValue = values.max
          Array(kind, minValue.toString, Q1.toString, Q2.toString, Q3.toString, maxValue.toString)
      }
      val jsons = Json.obj("ev" -> typeLong.map(_.drop(1).map(_.toDouble)), "tissue" -> typeLong.map(_ (0)))
      Json.toJson(jsons)
    })
  }

  def barPlot = Action.async { implicit request =>
    val startTime = System.currentTimeMillis()
    val data = formTool.geneIdForm.bindFromRequest().get
    fpkmDao.selectByGeneId(data.geneId).map { dbFpkms =>
      val fpkms = dbFpkms
      val tmpDir = tool.createTempDirectory("tmpDir")
      val buffer = ArrayBuffer(s"ID\tdata\tkind")
      buffer ++= fpkms.map { fpkm =>
        ArrayBuffer(s"${fpkm.samplename}(${fpkm.group})", fpkm.value, fpkm.group).mkString("\t")
      }
      FileUtils.writeLines(new File(tmpDir, "input.txt"), buffer.asJava)
      val pyFile = new File(Utils.pyPath, "barChart.py")
      val command = "python " + pyFile.getAbsolutePath
      val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(command))
      if (execCommand.isSuccess) {
        val divFile = new File(tmpDir, "div.txt")
        val divStr = FileUtils.readFileToString(divFile)
        val json = Json.obj("div" -> divStr)
        tool.deleteDirectory(tmpDir)
        Ok(json)
      } else {
        tool.deleteDirectory(tmpDir)
        Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
      }
    }
  }


  def getArrayByAllInfos(x: Seq[AllinfoRow]) = {
    x.map { y =>
      val geneIdStr = "<a target='_blank' title='more' href='" + routes.SearchController.getDetailInfo + "?geneId=" + y.geneid + "'>" + y.geneid + "</a>"
      val locus = y.chr + ":" + y.start + "-" + y.end
      Json.obj(
        "geneid" -> geneIdStr, "length" -> y.length, "chr" -> y.chr, "start" -> y.start, "end" -> y.end, "strand" -> y.strand,
        "cogclass" -> y.cogclass, "cogclassanno" -> y.cogclassanno, "goanno" -> y.goanno,
        "kegganno" -> y.kegganno, "kogclass" -> y.kogclass, "kogclassanno" -> y.kogclassanno, "pfamanno" -> y.pfamanno,
        "swissprotanno" -> y.swissprotanno, "trenbkanno" -> y.trenbkanno, "nranno" -> y.nranno, "ntanno" -> y.ntanno,
        "genename" -> y.genename, "eggNogClass" -> y.eggNogClass, "eggNogClassAnno" -> y.eggNogClassAnno
      )
    }
  }

}
