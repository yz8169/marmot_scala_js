package tool

import java.io.{File, FilenameFilter}
import java.nio.file.Files

import dao._
import javax.inject.Inject
import shared.Pojo._
import tool.Pojo._
import utils.Utils
import utils.Implicits._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import models.Tables._
import play.api.libs.json.Json
import play.api.mvc.RequestHeader

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * Created by yz on 2018/7/25
 */
class Tool @Inject()(modeDao: ModeDao, geneGoDao: GeneGoDao) {

  def createTempDirectory(prefix: String) = {
    if (isTestMode) Utils.testDir else Files.createTempDirectory(prefix).toFile
  }

  def isTestMode = {
    val mode = Utils.execFuture(modeDao.select)
    if (mode.test == "t") true else false
  }

  def deleteDirectory(direcotry: File) = {
    if (!isTestMode) Utils.deleteDirectory(direcotry)
  }

  val groupMap1 = mutable.LinkedHashMap(
    "summer active" -> ArrayBuffer("T01", "T02", "T03", "T04", "T05"),
    "torpor" -> ArrayBuffer("T11", "T12", "T14", "T15"),
    "autumn active" -> ArrayBuffer("T16", "T17", "T18", "T19", "T20"),
  )

  val groupMap2 = mutable.LinkedHashMap(
    "summer active" -> ArrayBuffer("T06", "T07", "T08", "T09", "T10"),
    "torpor" -> ArrayBuffer("T53", "T54", "T55", "T56", "T45"),
    "autumn active" -> ArrayBuffer("T21", "T22", "T23", "T24", "T25"),
  )

  val groupMap3 = mutable.LinkedHashMap(
    "Liver normal" -> ArrayBuffer("T06", "T07", "T08", "T09", "T10"),
    "Liver infected" -> ArrayBuffer("T26", "T27", "T28", "T29", "T30")
  )

  val groupMap4 = mutable.LinkedHashMap(
    "Lung normal" -> ArrayBuffer("T49", "T51", "T52"),
    "Lung infected" -> ArrayBuffer("T31", "T33", "T34", "T47")
  )

  val projectNames = ArrayBuffer("brain-hibernation", "liver-hibernation", "liver-plague", "lung-plague")

  def getDownColors = {
    val colorFile = new File(Utils.dataDir, "blue-middle.txt")
    getColors(colorFile)

  }

  def getUpColors = {
    val colorFile = new File(Utils.dataDir, "middle-red.txt")
    getColors(colorFile)

  }

  def getColors(colorFile: File) = {
    val colorLines = colorFile.lines
    colorLines.drop(1).mapOtherByColumns { columns =>
      columns(1)
    }
  }

  def getProjectExpDir(projectName: String) = {
    new File(Utils.expDir, projectName)
  }

  def getFilesByProjectName(projectName: String) = {
    val dir = new File(Utils.expDir, projectName)
    val filter = new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = name.contains("vs")
    }
    dir.listFiles(filter).toBuffer
  }

  def getVsNames(projectName: String) = {
    val files = getFilesByProjectName(projectName)
    files.map { file =>
      file.getName.replaceAll(s"${projectName}-", "").replaceAll(".txt", "")
    }
  }

  def getVsNamesV1(projectName: String, tissue: String) = {
    val files = getFilesByProjectName(projectName)
    files.filter { file =>
      file.getName.contains(tissue)
    }.map { file =>
      file.getName.replaceAll(s"${projectName}-", "").replaceAll(".txt", "")
    }
  }

  def getGeneGoJsons(ids: Seq[String]): Future[Seq[GeneGoJson]] = {
    geneGoDao.selectAll(ids).map { rows =>
      rows.map { row =>
        val content = Json.parse(row.content)
        GeneGoJson(row.geneId, content)
      }
    }
  }

  def getGeneGoSeqs(ids: Seq[String]) = {
    geneGoDao.selectAll(ids).map { rows =>
      rows.map { row =>
        val contents = (Json.parse(row.content) \\ "description").map(_.as[String])
        GeneGoSeq(row.geneId, contents)
      }
    }
  }

}

object Tool {

  def getChrData(locus: String) = {
    val chr = locus.split(":")(0)
    val start = locus.split(":")(1).split("-")(0).toInt
    val end = locus.split(":")(1).split("-")(1).toInt
    ChrData(chr, start, end)
  }

  def getGoTerm(info: String) = {
    "\\((GO:[^\\(\\)]*)\\)".r.findFirstMatchIn(info).map { pat =>
      pat.group(1)
    }.get
  }

  def getGeneKeggs(file: File) = {
    file.lines.drop(1).mapOtherByColumns { columns =>
      val geneId = columns(0)
      val infoStr = columns(1).trimQuote.trim
      val ko = infoStr.split("\\s+")(0)
      val description = infoStr.replaceAll("^[^\\s]*\\s+", "")
      GeneKeggRow(geneId, ko, description)
    }
  }

  def getGeneGoDatas(file: File) = {
    file.lines.drop(1).flatMapOtherByColumns { columns =>
      val geneId = columns(0)
      val infos = columns(1).trimQuote.trim
      infos.split(";;").map { info =>
        val category = info.split(":")(0)
        val term = info.replaceAll("^[^:]*:", "")
        val go = Tool.getGoTerm(term)
        val description = term.split("\\(")(0)
        GeneGoData(geneId, category, go, description)
      }
    }
  }

  def getGeneGos(geneGoDatas: Seq[GeneGoData]) = {
    geneGoDatas.groupBy(_.geneId).map { case (geneId, rows) =>
      val contens = rows.map { row =>
        Map("category" -> row.category, "term" -> row.term, "description" -> row.description)
      }
      val content = Json.stringify(Json.toJson(contens))
      GeneGoRow(geneId, content)
    }.toList
  }

  def getCds(file: File) = {
    val map = Tool.getSeqMap(file)
    map.map { case (key, cds) =>
      val id = key.split("\\s+")(0).replaceAll("^>", "")
      CdsRow(id, cds)
    }.toList
  }

  def getSeqMap(file: File) = {
    val map = mutable.LinkedHashMap[String, String]()
    var key = ""
    file.lines.foreach { line =>
      if (line.startsWith(">")) {
        key = line
        map += (key -> "")
      } else if (key != "") {
        map(key) += line
      }
    }
    map
  }

  def getPeps(file: File) = {
    val map = Tool.getSeqMap(file)
    map.map { case (key, cds) =>
      val id = key.split("\\s+")(0).replaceAll("^>", "")
      PepRow(id, cds)
    }.toList
  }

  def getOrthologs(file: File, sheetIndex: Int) = {
    file.xlsxLines(sheetIndex).drop(1).mapOtherByColumns { columns =>
      OrthologsRow(columns(0), columns(1), columns(2), columns(3), columns(4), columns(5), columns(6), columns(7))
    }
  }

  def getDbFile(againstType: String) = {
    againstType match {
      case "himalayanGene" => new File(Utils.seqsDir, "Marmot.pasa.changed.cds")
      case "himalayanGenome" => new File(Utils.seqsDir, "Marmot.masked.genome.fasta")
      case "alpineGene" => new File(Utils.seqsDir, "Alpine_marmot_longest.GCF_001458135.1_marMar2.1_genomic.cds")
      case "alpineGenome" => new File(Utils.seqsDir, "GCF_001458135.1_marMar2.1_genomic.fna")
      case "yellowGene" => new File(Utils.seqsDir, "longest.Yellow-bellied_marmot_genomic.cds")
      case "yellowGenome" => new File(Utils.seqsDir, "Yellow-bellied_marmot_genomic.fna")
    }
  }

  def getProteinDbFile(againstType: String) = {
    againstType match {
      case "himalayan" => new File(Utils.seqsDir, "Marmot.pasa.changed.pep")
      case "alpine" => new File(Utils.seqsDir, "Alpine_marmot_longest.GCF_001458135.1_marMar2.1_genomic.pep")
      case "yellow" => new File(Utils.seqsDir, "longest.Yellow-bellied_marmot_genomic.pep")
    }
  }

  def getIp(implicit request:RequestHeader)={
    request.remoteAddress
  }


}
