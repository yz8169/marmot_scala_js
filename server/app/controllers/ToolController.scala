package controllers

import java.io.File
import java.nio.file.Files

import javax.inject.Inject
import dao._
import models.Tables.FpkmRow
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import tool.{FormTool, Tool}
import utils.{ExecCommand, Utils}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import utils.Implicits._

class ToolController @Inject()(cc: ControllerComponents, fpkmDao: FpkmDao, pepDao: PepDao, tool: Tool, formTool: FormTool,
                               fpkmMessageDao: FpkmMessageDao) extends AbstractController(cc) {

  case class GeneWiseData(method: String, proteinSeq: String, geneId: String, dnaSeq: String, para: Boolean,
                          pretty: Boolean, genes: Boolean, trans: Boolean, cdna: Boolean, embl: Boolean, ace: Boolean,
                          gff: Boolean, diana: Boolean, init: String, splice: String, random: String, alg: String)

  val geneWiseForm = Form(
    mapping(
      "method" -> text,
      "proteinSeq" -> text,
      "geneId" -> text,
      "dnaSeq" -> text,
      "para" -> boolean,
      "pretty" -> boolean,
      "genes" -> boolean,
      "trans" -> boolean,
      "cdna" -> boolean,
      "embl" -> boolean,
      "ace" -> boolean,
      "gff" -> boolean,
      "diana" -> boolean,
      "init" -> text,
      "splice" -> text,
      "random" -> text,
      "alg" -> text
    )(GeneWiseData.apply)(GeneWiseData.unapply)
  )


  def keggEnrichBefore = Action { implicit request =>
    Ok(views.html.tool.keggEnrich())
  }

  def muscleBefore = Action { implicit request =>
    Ok(views.html.tool.muscle())
  }

  case class SvgData(svgHtml: String)

  val svgForm = Form(
    mapping(
      "svgHtml" -> text
    )(SvgData.apply)(SvgData.unapply)
  )

  def downloadTree = Action { implicit request =>
    val data = svgForm.bindFromRequest().get
    val dataFile = Files.createTempFile("tmp", ".svg").toFile
    val str = data.svgHtml.replaceAll("</svg>", "\n" + Utils.phylotreeCss + "</svg>")
    FileUtils.writeStringToFile(dataFile, str, "UTF-8")
    Ok.sendFile(dataFile, onClose = () => {
      Files.deleteIfExists(dataFile.toPath)
    }).withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> "attachment; filename=tree.svg",
      CONTENT_TYPE -> "application/x-download"
    )
  }

  def keggEnrich = Action(parse.multipartFormData) { implicit request =>
    val data = formTool.keggEnrichForm.bindFromRequest.get
    println(data)
    val tmpDir = tool.createTempDirectory("tmpDir")
    val diffFile = new File(tmpDir, "diff.txt")
    data.geneId.map { geneId =>
      val buffer = geneId.split(",").toBuffer
      FileUtils.writeLines(diffFile, buffer.asJava)
    }.getOrElse {
      val file = request.body.file("file").get
      file.ref.moveTo(diffFile, replace = true)
      Utils.dealInputFile(diffFile)
    }
    val outFile = new File(tmpDir, "out.txt")
    val identifyFile = new File(Utils.path, "identify.pl")
    val geneListFile = new File(Utils.enrichDir, s"${data.database}_list.txt")
    val annoFile = new File(Utils.enrichDir, s"${data.database}_kegg_anno.txt")
    val command =
      s"""
         |dos2unix *
         |perl ${identifyFile.unixPath} -study ${diffFile.unixPath} -population ${geneListFile.unixPath} -association ${annoFile.unixPath} -m ${data.method} -n ${data.fdr} -c ${data.cutoff} -o ${outFile.unixPath} -maxp ${data.pValue}
       """.stripMargin
    val execCommand = Utils.callLinuxScript(tmpDir, shBuffer = ArrayBuffer(command))
    if (execCommand.isSuccess) {
      val buffer = FileUtils.readLines(outFile).asScala.dropWhile(x => !x.startsWith("#")).drop(1)
      val jsons = buffer.filter(x => StringUtils.isNotBlank(x)).map { x =>
        val columns = x.split("\t")
        val pValue = columns(5).toDouble.formatted("%.3f").toString
        val cPValue = if (columns(6).isDouble) {
          columns(6).toDouble.toFixed(3)
        } else columns(6)
        val link = s"<a href='${columns(8)}' target='_blank'>link</a>"
        Json.obj("term" -> columns(0), "database" -> columns(1), "id" -> columns(2), "inputNumber" -> columns(3),
          "backgroundNumber" -> columns(4), "pValue" -> pValue, "cPValue" -> cPValue, "input" -> columns(7),
          "hyperlink" -> link)
      }
      tool.deleteDirectory(tmpDir)
      Ok(Json.toJson(jsons))
    } else {
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def muscle = Action(parse.multipartFormData) { implicit request =>
    val data = formTool.muscleForm.bindFromRequest.get
    val tmpDir = Files.createTempDirectory("tmpDir").toFile
    val seqFile = new File(tmpDir, "seq.txt")
    data.queryText.map { queryText =>
      FileUtils.writeStringToFile(seqFile, queryText)
    }.getOrElse {
      val file = request.body.file("file").get
      file.ref.moveTo(seqFile, replace = true)
    }

    val size = FileUtils.sizeOf(seqFile);
    val mSize = size / (1024 * 1024);
    if (mSize >= 1) {
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> "Sequences is too long(over 1MB)!"))
    } else {
      val outFile = new File(tmpDir, "out.txt")
      val execCommand = new ExecCommand
      val commandBuffer = ArrayBuffer[String](Utils.muscleCommand + " -in " + seqFile.getAbsolutePath + " -verbose " + " -log " +
        " -fasta " + " -out " + outFile.getAbsolutePath + " -group")
      val treeFile = new File(tmpDir, "tree.txt")
      data.tree match {
        case "none" =>
        case "tree1" => commandBuffer ++= ArrayBuffer("-tree1", treeFile.getAbsolutePath)
        case "tree2" => commandBuffer ++= ArrayBuffer("-tree2", treeFile.getAbsolutePath)
      }
      val command1 = commandBuffer.mkString(" ")
      execCommand.exec(command1, tmpDir)
      if (execCommand.isSuccess) {
        val str = FileUtils.readFileToString(outFile)
        val treeStr = if (treeFile.exists()) {
          FileUtils.readFileToString(treeFile)
        } else ""
        Utils.deleteDirectory(tmpDir)
        Ok(Json.obj("out" -> str, "tree" -> treeStr))
      } else {
        Utils.deleteDirectory(tmpDir)
        Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
      }
    }
  }

  def geneWise = Action { implicit request =>
    val data = geneWiseForm.bindFromRequest.get
    val tmpDir = Files.createTempDirectory("tmpDir").toFile
    val proteinFile = new File(tmpDir, "protein.fa")
    val buffer = data.method match {
      case "db" =>
        val pep = Utils.execFuture(pepDao.selectByGeneId(data.geneId)).get
        ArrayBuffer(">" + pep.geneid, pep.pep)
      case "hand" => ArrayBuffer(data.proteinSeq)
    }
    FileUtils.writeLines(proteinFile, buffer.asJava)
    val dnaFile = new File(tmpDir, "dna.fa")
    FileUtils.writeStringToFile(dnaFile, data.dnaSeq, "UTF-8")
    val outFile = new File(tmpDir, "out.txt")
    val execCommand = new ExecCommand
    val commandBuffer = ArrayBuffer("genewise " + proteinFile.getAbsolutePath + " " + dnaFile.getAbsolutePath + " -kbyte 4000 " +
      " -init " + data.init + " -null  " + data.random + " -alg " + data.alg)
    if (data.para) commandBuffer += "-para"
    if (data.pretty) commandBuffer += "-pretty"
    if (data.genes) commandBuffer += "-genes"
    if (data.trans) commandBuffer += "-trans"
    if (data.cdna) commandBuffer += "-cdna"
    if (data.embl) commandBuffer += "-embl"
    if (data.ace) commandBuffer += "-ace"
    if (data.gff) commandBuffer += "-gff"
    if (data.diana) commandBuffer += "-diana"
    data.splice match {
      case "flat" =>
      case "model" => commandBuffer += "-nosplice_gtag"
    }
    val command1 = commandBuffer.mkString(" ")
    execCommand.exec(command1, outFile, tmpDir)
    if (execCommand.isSuccess) {
      val str = FileUtils.readFileToString(outFile)
      Utils.deleteDirectory(tmpDir)
      Ok(Json.toJson(str))
    } else {
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def getExampleFile = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.path, s"example/${data.fileName}")
    Ok.sendFile(file).as(TEXT)
  }

  def getExampleText = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.path, s"example/${data.fileName}")
    val str = FileUtils.readFileToString(file)
    Ok(str)
  }

  def geneWiseBefore = Action { implicit request =>
    Ok(views.html.tool.geneWise())
  }

  def goEnrichBefore = Action { implicit request =>
    Ok(views.html.tool.goEnrich())
  }

  def goEnrich = Action(parse.multipartFormData) { implicit request =>
    val data = formTool.keggEnrichForm.bindFromRequest.get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val diffFile = new File(tmpDir, "diff.txt")
    data.geneId.map { geneId =>
      val buffer = geneId.split(",").toBuffer
      FileUtils.writeLines(diffFile, buffer.asJava)
    }.getOrElse {
      val file = request.body.file("file").get
      file.ref.moveTo(diffFile, replace = true)
      Utils.dealInputFile(diffFile)
    }
    val outFile = new File(tmpDir, "out.txt")
    val execCommand = new ExecCommand
    val enrichFile = new File(Utils.path, "goatools-0.5.7/scripts/find_enrichment.py")
    val geneListFile = new File(Utils.enrichDir, s"${data.database}_list.txt")
    val annoFile = new File(Utils.enrichDir, s"${data.database}_go_anno.txt")
    val command1 = s"${Utils.goPy} " + enrichFile.getAbsolutePath + " --pval " + data.pValue +
      " --output " + outFile.getAbsolutePath + " " + diffFile.getAbsolutePath + " " + geneListFile.getAbsolutePath + " " +
      annoFile.getAbsolutePath
    execCommand.exec(command1, tmpDir)
    if (execCommand.isSuccess) {
      val buffer = FileUtils.readLines(outFile).asScala.drop(1)
      val jsons = buffer.map { x =>
        val columns = x.split("\t")
        val pu = columns(5).toDouble.formatted("%.3f").toString
        Json.obj("id" -> columns(0), "enrichment" -> columns(1), "description" -> columns(2), "ratio_in_study" -> columns(3),
          "ratio_in_pop" -> columns(4), "p_uncorrected" -> pu, "p_bonferroni" -> columns(6), "p_holm" -> columns(7),
          "p_sidak" -> columns(8), "p_fdr" -> columns(9), "namespace" -> columns(10), "genes_in_study" -> columns(11))
      }
      tool.deleteDirectory(tmpDir)
      Ok(Json.toJson(jsons))
    } else {
      tool.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def pCABefore = Action { implicit request =>
    Ok(views.html.tool.pCA())
  }

  def getAllSampleNames = Action.async {
    fpkmMessageDao.selectAll.flatMap { fpkmMessage =>
      fpkmDao.selectByGeneId(fpkmMessage.head.geneid)
    }.map { fpkms =>
      val array = fpkms.map { fpkm =>
        s"${fpkm.samplename}(${fpkm.group})"
      }
      Ok(Json.toJson(array))
    }
  }

  case class PcaData(offerMethod: String, sampleName: Seq[String])

  val pcaForm = Form(
    mapping(
      "offerMethod" -> text,
      "sampleName" -> seq(text)
    )(PcaData.apply)(PcaData.unapply)
  )

  case class CheatmapData(sampleName: Seq[String])

  val cheatmapForm = Form(
    mapping(
      "sampleName" -> seq(text)
    )(CheatmapData.apply)(CheatmapData.unapply)
  )

  def productDataBuffer(sampleNames: Seq[String], dbFpkms: Seq[FpkmRow], f: Double => Double = (x: Double) => x) = {
    val buffer = mutable.Buffer[mutable.Buffer[String]]()
    val headers = sampleNames.toBuffer
    headers.prepend("ID")
    buffer += headers
    dbFpkms.groupBy(_.geneid).foreach { case (geneId, fpkms) =>
      val array = mutable.Buffer[String](geneId)
      val values = sampleNames.map { y =>
        val sampleName = y.replaceAll("\\(.*", "")
        val groupName = y.replaceAll(".*\\(", "").replaceAll("\\)", "")
        val fpkm = fpkms.find(z => z.samplename == sampleName && z.group == groupName).get
        f(fpkm.value)
      }
      if (values.exists(_ != 0)) {
        buffer += (array ++= values.map(_.toString))
      }
    }
    buffer
  }


  def plotPCA = Action.async(parse.multipartFormData) { implicit request =>
    val data = cheatmapForm.bindFromRequest.get
    fpkmDao.selectAll.map { dbFpkms =>
      val tmpDir = tool.createTempDirectory("tmpDir")
      val dataFile = new File(tmpDir, "deal.txt")
      if (data.sampleName.isEmpty) {
        val file = request.body.file("file").get
        file.ref.moveTo(dataFile, replace = true)
        Utils.dealInputFile(dataFile)
      } else {
        val sampleNames = data.sampleName
        val buffer = productDataBuffer(sampleNames, dbFpkms)
        FileUtils.writeLines(dataFile, buffer.map(_.mkString("\t")).asJava)
      }
      val rFile = new File(Utils.rPath, "pca.R")
      val command = "Rscript  --restore --no-save " + rFile.getAbsolutePath
      val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(command))
      if (!execCommand.isSuccess) {
        tool.deleteDirectory(tmpDir)
        Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
      } else {
        val outputFile = new File(tmpDir, "out.txt")
        val pyFile = new File(Utils.pyPath, "pca.py")
        val command = if (data.sampleName.isEmpty) {
          s"""
             |python ${pyFile} -m hand
             """.stripMargin
        } else {
          val groupFile = new File(tmpDir, "group.txt")
          val groupLines = ArrayBuffer("Sample\tGroup")
          groupLines ++= data.sampleName.map { sampleName =>
            val groupName = sampleName.replaceAll(".*\\(", "").replaceAll("\\)", "")
            s"${sampleName}\t${groupName}"
          }
          FileUtils.writeLines(groupFile, groupLines.asJava)
          s"""
             |python ${pyFile} -m db
             """.stripMargin
        }
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
  }

  def hClusterBefore = Action { implicit request =>
    Ok(views.html.tool.hCluster())
  }

  def plotCluster = Action.async(parse.multipartFormData) { implicit request =>
    val data = cheatmapForm.bindFromRequest.get
    fpkmDao.selectAll.map { dbFpkms =>
      val tmpDir = Files.createTempDirectory("tmpDir").toFile
      val dataFile = new File(tmpDir, "deal.txt")
      if (data.sampleName.isEmpty) {
        val file = request.body.file("file").get
        file.ref.moveTo(dataFile, replace = true)
        Utils.dealInputFile(dataFile)
      } else {
        val sampleNames = data.sampleName
        val buffer = productDataBuffer(sampleNames, dbFpkms)
        FileUtils.writeLines(dataFile, buffer.map(_.mkString("\t")).asJava)
        Utils.dataDuplicate(dataFile)
      }

      val pyFile = new File(Utils.pyPath, "dendrogram.py")
      val pyCommand = "python " + pyFile.getAbsolutePath
      val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(pyCommand))
      if (!execCommand.isSuccess) {
        Utils.deleteDirectory(tmpDir)
        Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
      } else {
        val divFile = new File(tmpDir, "div.txt")
        val divStr = FileUtils.readFileToString(divFile)
        val json = Json.obj("div" -> divStr)
        Utils.deleteDirectory(tmpDir)
        Ok(json)
      }
    }
  }

  def cHeatMapBefore = Action { implicit request =>
    Ok(views.html.tool.cHeatMap())
  }

  def cHeatMap = Action.async(parse.multipartFormData) { implicit request =>
    val data = cheatmapForm.bindFromRequest.get
    fpkmDao.selectAll.map { dbFpkms =>
      val tmpDir = tool.createTempDirectory("tmpDir")
      val dataFile = new File(tmpDir, "deal.txt")
      if (data.sampleName.isEmpty) {
        val file = request.body.file("file").get
        file.ref.moveTo(dataFile, replace = true)
        Utils.dealInputFile(dataFile)
      } else {
        val sampleNames = data.sampleName
        println(sampleNames)
        val buffer = productDataBuffer(sampleNames, dbFpkms, (x: Double) => Utils.log2(x + 1))
        FileUtils.writeLines(dataFile, buffer.map(_.mkString("\t")).asJava)
        Utils.dataDuplicate(dataFile)
      }

      val rFile = new File(Utils.rPath, "cCoefficient.R")
      val rCommand = "Rscript  --restore --no-save " + rFile.getAbsolutePath
      val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(rCommand))
      if (!execCommand.isSuccess) {
        tool.deleteDirectory(tmpDir)
        Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
      } else {
        val outputFile = new File(tmpDir, "out.txt")
        val lines = FileUtils.readLines(outputFile).asScala
        val colNames = lines(0).split("\t")
        val rowNames = lines.drop(1).map(_.split("\t")(0))
        val expressions = for (i <- colNames.indices; j <- rowNames.indices) yield {
          val value = lines(i + 1).split("\t")(j + 1).toDouble
          Array(i, j, value)
        }
        val max = expressions.toArray.map(x => x(2)).max
        val min = expressions.toArray.map(x => x(2)).min
        val jsons = Array(Json.obj("expression" -> expressions, "treatment" -> colNames, "gt" -> rowNames,
          "max" -> max, "min" -> min))
        tool.deleteDirectory(tmpDir)
        Ok(Json.toJson(jsons))
      }

    }
  }

  def enrichBefore = Action { implicit request =>
    Ok(views.html.tool.enrich())
  }

  def mummerBefore = Action { implicit request =>
    Ok(views.html.tool.mummer())
  }

  def mummer = Action(parse.multipartFormData) { implicit request =>
    val data = formTool.mummerForm.bindFromRequest.get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val targetFile = new File(tmpDir, "target.fa")
    data.targetText.map { targetText =>
      FileUtils.writeStringToFile(targetFile, targetText)
    }.getOrElse {
      val file = request.body.file("targetFile").get
      file.ref.moveTo(targetFile, replace = true)
    }
    val queryFile = new File(tmpDir, "query.fa")
    data.queryText.map { queryText =>
      FileUtils.writeStringToFile(queryFile, queryText)
    }.getOrElse {
      val file = request.body.file("queryFile").get
      file.ref.moveTo(queryFile, replace = true)
    }
    val command =
      s"""
         |perl ${Utils.svBin}/SV_finder.pl ${targetFile} ${queryFile} --prefix data --tfix target --qfix query --nocomp --outdir output --locate
       """.stripMargin
    val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(command))
    val json = if (execCommand.isSuccess) {
      val file1 = new File(tmpDir, "output/02.Lastz/Target-Query.parallel.png")
      val base641 = Utils.getBase64Str(file1)
      val file2 = new File(tmpDir, "output/02.Lastz/Target-Query.xoy.png")
      val base642 = Utils.getBase64Str(file2)
      val axtFile = new File(tmpDir, "output/02.Lastz/all.axt")
      val axtString = FileUtils.readFileToString(axtFile)
      Json.obj("base641" -> base641, "base642" -> base642, "axtString" -> axtString)
    } else {
      Json.obj("valid" -> "false", "message" -> execCommand.getErrStr)
    }
    tool.deleteDirectory(tmpDir)
    Ok(json)
  }


}
