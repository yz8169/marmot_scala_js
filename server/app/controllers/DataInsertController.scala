package controllers

import java.io.File

import javax.inject.Inject
import models.Tables._
import org.apache.commons.io.FileUtils
import play.api.mvc._
import dao._
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import tool.Pojo.GeneGoData
import tool.Tool
import utils.Utils

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import utils.Implicits._


class DataInsertController @Inject()(cc: ControllerComponents, fpkmMessageDao: FpkmMessageDao,
                                     fpkmDao: FpkmDao, cdsDao: CdsDao, pepDao: PepDao, allInfoDao: AllInfoDao,
                                     tool: Tool, hibernationDiffGeneDao: HibernationDiffGeneDao, goDao: GoDao,
                                     keggDao: KeggDao, basicDao: BasicDao, geneGoDao: GeneGoDao,
                                     geneBlockDao: GeneBlockDao, plagueListDao: PlagueListDao,
                                     geneKeggDao: GeneKeggDao, orthologsDao: OrthologsDao,
                                     otherOrthologsDao: OtherOrthologsDao,
                                     hiberListDao: HiberListDao) extends AbstractController(cc) {

  def insertGeneBlock = Action {
    val parent = new File("E:\\marmot\\test")
    val file = new File(parent, "h_vs_a.txt")
    val rows1 = getGeneBlockRows(file, "Himalayan_marmot vs Alpine marmot")
    val file2 = new File(parent, "h_vs_y.txt")
    val rows2 = getGeneBlockRows(file2, "Himalayan_marmot vs Yellow-bellied marmot")
    val rows = rows1 ++ rows2

    Await.result(geneBlockDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(geneBlockDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

  def getGeneBlockRows(file: File, kind: String) = {
    val buffer = file.lines.drop(2)
    var key = ""
    val rows = ArrayBuffer[GeneBlockRow]()
    buffer.foreach { line =>
      val columns = line.split("\t")
      if (columns.size < 2) {
        key = line.trim
        rows
      } else {
        val bGeneInfos = columns(9).split(";")
        val row = GeneBlockRow(0, columns(0), kind, columns(1), columns(2).toInt, columns(3).toInt, columns(4), columns(7),
          columns(8), bGeneInfos(0), bGeneInfos(1).toInt, bGeneInfos(2).toInt, bGeneInfos(3), columns(5), columns(6), key)
        rows += row
      }
    }
    rows

  }


  def insertOrthologs = Action {
    val parent = new File("D:\\marmot\\test")
    val file = new File(parent, "附件3.3.7.xlsx")
    val rows = Tool.getOrthologs(file, 0) ++
      Tool.getOrthologs(new File(parent, "data4search.xlsx"), 3) ++
      Tool.getOrthologs(new File(parent, "data4search.xlsx"), 6)
    Await.result(orthologsDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(orthologsDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

  def insertPlagueList = Action {
    val parent = new File("E:\\marmot\\test")
    val file = new File(parent, "DEG-plague-hibernation.xlsx")
    val buffer = Utils.xlsx2Lines(file, 0)
    val geneIds = ArrayBuffer[String]()
    val rows = buffer.map { line =>
      val columns = line.split("\t")
      PlagueListRow(columns(0))
    }
    Await.result(plagueListDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(plagueListDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

  def insertHiberList = Action {
    val parent = new File("E:\\marmot\\test")
    val file = new File(parent, "DEG-plague-hibernation.xlsx")
    val buffer = Utils.xlsx2Lines(file, 1)
    val geneIds = ArrayBuffer[String]()
    val rows = buffer.map { line =>
      val columns = line.split("\t")
      HiberListRow(columns(0))
    }
    Await.result(hiberListDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(hiberListDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

  def insertOtherOrthologs = Action {
    val parent = new File("E:\\marmot\\test")
    val file = new File(parent, "2marmots-annotation&ortholog.xlsx")
    val buffer = Utils.xlsx2Lines(file, 2)
    val rows = buffer.drop(1).map { line =>
      val columns = line.split("\t")
      OtherOrthologsRow(columns(0), columns(1), columns(2))
    }
    Await.result(otherOrthologsDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(otherOrthologsDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }


  def insertGeneKegg = Action {
    val parent = new File("D:\\marmot\\test")
    val file = new File(parent, "附件3.3.6.xlsx")
    val buffer = Utils.xlsx2Lines(file, 1)
    val rows = buffer.drop(1).map { line =>
      val columns = line.split("\t")
      GeneKeggRow(columns(0), columns(1), columns(2))
    } ++ Tool.getGeneKeggs(new File(parent, "alpine_gene_kegg.txt")) ++
      Tool.getGeneKeggs(new File(parent, "yellow_gene_kegg.txt"))
    Await.result(geneKeggDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(geneKeggDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }


  def insertGeneGo = Action {
    val parent = new File("D:\\marmot\\test")
    val file = new File(parent, "gene_go.txt")
    val buffer = file.lines
    var geneId = ""
    var category = ""
    val geneGoDatas = buffer.drop(1).map { line =>
      val columns = line.split("\t")
      if (StringUtils.isNotBlank(columns(0))) geneId = columns(0)
      if (StringUtils.isNotBlank(columns(1))) category = columns(1)
      GeneGoData(geneId, category, columns(2), columns(3))
    }.distinct ++ Tool.getGeneGoDatas(new File(parent, "alpine_gene_go.txt")) ++
      Tool.getGeneGoDatas(new File(parent, "yellow_gene_go.txt"))
    val rows = Tool.getGeneGos(geneGoDatas)
    Await.result(geneGoDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(geneGoDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

  def insertBasic = Action {
    val parent = new File("D:\\marmot\\test")
    val file = new File(parent, "附件3.3.1.txt")
    val rows = file.lines.drop(1).mapOtherByColumns { columns =>
      BasicRow(columns(0), "Himalayan marmot", columns(1), columns(2), columns(3), columns(4).toInt, columns(5).toInt,
        columns(6))
    } ++ (new File(parent, "alpine_search.txt")).lines.drop(1).mapOtherByColumns { tmpColumns =>
      val columns = tmpColumns.padTo(5, "NA")
      val locus = columns(2)
      val chrData = Tool.getChrData(locus)
      BasicRow(columns(0), "Alpine marmot", columns(1), columns(4), chrData.chr, chrData.start, chrData.end,
        columns(3))
    } ++ (new File(parent, "yellow_search.txt")).lines.drop(1).mapOtherByColumns { tmpColumns =>
      val columns = tmpColumns.padTo(5, "NA")
      val locus = columns(2)
      val chrData = Tool.getChrData(locus)
      BasicRow(columns(0), "Yellow-bellied marmot", columns(1), columns(4), chrData.chr, chrData.start, chrData.end,
        columns(3))
    }
    Await.result(basicDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(basicDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }


  def insertKegg = Action {
    val parent = new File("E:\\marmot\\test")
    val file = new File(parent, "kegg.txt")
    val buffer = FileUtils.readLines(file).asScala
    val rows1 = buffer.drop(1).map { line =>
      val columns = line.split("\t")
      KeggRow(columns(0), "Himalayan marmot", columns(1), columns(2).toInt, columns(3))
    }
    val rows2 = Utils.xlsx2Lines(new File(parent, "Alpine-marmot-GO-KEGG.xlsx"), 0).drop(1).map { line =>
      val columns = line.split("\t")
      KeggRow(columns(1), "Alpine marmot", columns(0), columns(2).toInt, columns.drop(3).mkString(";"))
    }
    val rows3 = Utils.xlsx2Lines(new File(parent, "huangfu-marmot-GO-KEGG.xlsx"), 1).drop(1).map { line =>
      val columns = line.split("\t")
      KeggRow(columns(1), "Yellow-bellied marmot", columns(0), columns(2).toInt, columns.drop(3).mkString(";"))
    }
    val rows = rows1 ++ rows2 ++ rows3
    Await.result(keggDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(keggDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }


  def insertGo = Action {
    val parent = new File("D:\\marmot\\test")
    val file = new File(parent, "go.txt")
    val buffer = FileUtils.readLines(file).asScala
    val rows1 = buffer.map { line =>
      val columns = line.split("\t")
      GoRow(0, "Himalayan marmot", columns(2), columns(3).toInt, columns(1), columns.drop(4).mkString(";"), columns(0))
    }
    val rows2 = Utils.xlsx2Lines(new File(parent, "Alpine-marmot-GO-KEGG.xlsx"), 1).drop(1).
      filter { line =>
        val columns = line.split("\t")
        columns.size > 3 && (columns(1).contains(":") | columns(1).contains("："))
      }.map { line =>
      val columns = line.split("\t")
      println(columns(1))
      val infos = columns(1).split(":|：")
      val desc = infos(1)
      val kind = infos(0)
      GoRow(0, "Alpine marmot", columns(0), columns.drop(2).size, desc, columns.drop(2).mkString(";"), kind)
    }
    val rows3 = Utils.xlsx2Lines(new File(parent, "huangfu-marmot-GO-KEGG.xlsx"), 0).drop(1).
      filter { line =>
        val columns = line.split("\t")
        columns.size > 3 && columns(1).contains(":")
      }.map { line =>
      val columns = line.split("\t")
      val infos = columns(1).split(":")
      val desc = infos(1)
      val kind = infos(0)
      GoRow(0, "Yellow-bellied marmot", columns(0), columns.drop(2).size, desc, columns.drop(2).mkString(";"), kind)
    }
    val rows = rows1 ++ rows2 ++ rows3
    Await.result(goDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(goDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }


  def insertHibernationDiffGenes = Action {
    val parent = new File("E:\\marmot")
    val file = new File(parent, "冬眠物种差异基因总结-20181219.txt")
    val buffer = FileUtils.readLines(file).asScala.drop(1)
    val rows = buffer.map { line =>
      val columns = line.split("\t").map(_.replaceAll("^\"", "").replaceAll("\"$", ""))
      val regionStr = columns(7)
      case class RegionData(str: Option[String], start: Option[Int], end: Option[Int])
      val region = if (regionStr.toLowerCase == "na") {
        RegionData(None, None, None)
      } else {
        val str = regionStr.split(":")(0)
        val start = regionStr.split(":")(1).split(",")(0).toInt
        val end = regionStr.split(":")(1).split(",")(1).toInt
        RegionData(Some(str), Some(start), Some(end))
      }
      HibernationDiffGeneRow(0, columns(0), columns(1), columns(2), columns(3), columns(4), columns(5).toDouble,
        columns(6), region.str, region.start, region.end)
    }
    Await.result(hibernationDiffGeneDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(hibernationDiffGeneDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

  def insertAllInfo = Action {
    val file = new File(parent, "Marmot.gene.annotation.txt")
    val buffer = FileUtils.readLines(file).asScala.drop(1)
    val map = buffer.filter { line =>
      line.split("\t").size == 14
    }.map { line =>
      val columns = line.split("\t")
      (columns(0), columns)
    }.toMap
    val fpkmMessages = Await.result(fpkmMessageDao.selectAll, Duration.Inf)
    val rows = fpkmMessages.map { fpkmMessage =>
      val array = new Array[String](14)
      val newArray = array.map(x => "NA")
      val columns = map.getOrElse(fpkmMessage.geneid, newArray)
      val ntAnno = columns(13)
      val geneNames = "\\([^\\(\\)\\s+]*\\),".r.findAllIn(ntAnno).toBuffer
      val geneName = if (geneNames.isEmpty) {
        "NA"
      } else geneNames.last.drop(1).dropRight(2)
      val goAnno = columns(3)
      val gos = "\\(GO:[^\\(\\)]*\\)".r.findAllIn(goAnno).toBuffer.distinct
      val goTerm = if (gos.isEmpty) {
        "NA"
      } else {
        gos.map(_.drop(1).dropRight(1)).mkString(";")
      }
      val keggAnno = columns(4)
      val kos = "K\\d+".r.findAllIn(keggAnno).toBuffer.distinct
      val keggTerm = if (kos.isEmpty) {
        "NA"
      } else {
        kos.head
      }
      val last = keggAnno.lastIndexOf("|")
      val keggAnno1 = if (last != -1) {
        keggAnno.drop(last + 1)
      } else {
        keggAnno
      }
      AllinfoRow(fpkmMessage.geneid, geneName, fpkmMessage.length, fpkmMessage.chr, fpkmMessage.start, fpkmMessage.end, fpkmMessage.strand,
        columns(1), columns(2), goTerm, goAnno, keggTerm, keggAnno1, columns(5), columns(6),
        columns(7), columns(8), columns(9), columns(10), columns(11), columns(12), ntAnno)
    }
    Await.result(allInfoDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(allInfoDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")
  }

  def insertFpkmMessage = Action {
    val file = new File(parent, "fpkm_message.txt")
    val buffer = FileUtils.readLines(file).asScala
    val rows = buffer.map { x =>
      val columns = x.split("\t").map { y =>
        y.replaceAll("^\"", "").replaceAll("\"$", "")
      }
      val chr = columns(0)
      val start = columns(3).toInt
      val end = columns(4).toInt
      val length = (end - start).abs + 1
      val geneId = columns.last.replaceAll("ID=", "")
      FpkmmessageRow(geneId, length, chr, start, end, columns(6))
    }
    Await.result(fpkmMessageDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(fpkmMessageDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")
  }

  def getGroup(lines: mutable.Buffer[String]) = {
    val samples = lines(1).split("\t").toBuffer
    val headers = lines.headers
    val notEmptyHeaders = headers.zipWithIndex.filter { case (v, i) =>
      StringUtils.isNotEmpty(v)
    }
    notEmptyHeaders.zipWithIndex.map { case (v, i) =>
      if (i < notEmptyHeaders.length - 1) {
        (v._2, notEmptyHeaders(i + 1)._2)
      } else (v._2, samples.length)
    }.flatMap { case (start, end) =>
      samples.slice(start, end).map(x => (x, headers(start)))
    }.toMap
  }


  def getRows(tmpLines: ArrayBuffer[String]) = {
    val groupMap = getGroup(tmpLines)
    val lines = tmpLines.drop(1)
    val headers = lines.head.split("\t").drop(2)
    lines.drop(1).flatMap { line =>
      val columns = line.split("\t")
      val geneId = columns(0)
      columns.drop(2).zip(headers).map { case (value, header) =>
        FpkmRow(geneId, header, value.toDouble, groupMap(header))
      }
    }

  }

  def insertFpkm = Action.async {
    val parent = new File("E:\\marmot\\test")
    val file = new File(parent, "附件2.xlsx")
    val lines1 = Utils.xlsx2Lines(file, 0)
    val rows1 = getRows(lines1)
    val rows = (rows1).sortBy(x => (x.geneid, x.samplename))
    fpkmDao.deleteAll.map { x =>
      val startTime = System.currentTimeMillis()
      val rowsSize = rows.size
      val num = 5000
      var index = 0
      rows.grouped(num).foreach { x =>
        Await.result(fpkmDao.insertAll(x), Duration.Inf)
        index = index + 1
        val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
        println(percent + "\t" + Utils.getTime(startTime))
      }
      println("insert table successfully!" + Utils.getTime(startTime))
      Ok("success!")
    }


  }

  val parent = new File("D:\\marmot\\test")

  def insertCds = Action {
    val file = new File(parent, "Marmot.genes.cds.fa")
    val rows = Tool.getCds(file) ++
      Tool.getCds((new File(parent, "Alpine_marmot_longest.GCF_001458135.1_marMar2.1_genomic.cds"))) ++
      Tool.getCds((new File(parent, "longest.Yellow-bellied_marmot_genomic.cds")))
    Await.result(cdsDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(cdsDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

  def insertPep = Action {
    val file = new File(parent, "Marmot.genes.pep.fa")
    val rows = Tool.getPeps(file) ++
      Tool.getPeps((new File(parent, "Alpine_marmot_longest.GCF_001458135.1_marMar2.1_genomic.pep"))) ++
      Tool.getPeps((new File(parent, "longest.Yellow-bellied_marmot_genomic.pep")))

    Await.result(pepDao.deleteAll, Duration.Inf)
    val startTime = System.currentTimeMillis()
    val rowsSize = rows.size
    val num = 1000
    var index = 0
    rows.grouped(num).foreach { x =>
      Await.result(pepDao.insertAll(x), Duration.Inf)
      index = index + 1
      val percent = if ((index * num * 100) / rowsSize >= 100) "100%" else (index * num * 100) / rowsSize + "%"
      println(percent + "\t" + Utils.getTime(startTime))
    }
    println("insert table successfully!" + Utils.getTime(startTime))
    Ok("success!")

  }

}
