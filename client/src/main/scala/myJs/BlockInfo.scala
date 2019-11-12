package myJs

import myJs.Utils._
import myJs.myPkg.{ColumnOptions, TableOptions}
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import myPkg.Implicits._

@JSExportTopLevel("BlockInfo")
object BlockInfo {
  var blockId = ""

  @JSExport("init")
  def init(id: String) = {
    blockId = id
    refreshBasicInfo

  }

  case class GeneInfo(chr: String, min: Int, max: Int)

  def refreshBasicInfo = {
    val url = g.jsRoutes.controllers.GeneBlockController.blockInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?blockId=${blockId}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val aChr = rs.head.myGet("aChr")
      val aStart = rs.map(x => x.myGetInt("aStart")).min
      val aEnd = rs.map(x => x.myGetInt("aEnd")).max
      val aLocus = s"${aChr}:${aStart}-${aEnd}"
      val aGeneInfo = GeneInfo(aChr, aStart, aEnd)
      val bChr = rs.head.myGet("bChr")
      val bStart = rs.map(x => x.myGet("bStart").toInt).min
      val bEnd = rs.map(x => x.myGet("bEnd").toInt).max
      val bLocus = s"${bChr}:${bStart}-${bEnd}"
      val bGeneInfo = GeneInfo(bChr, bStart, bEnd)
      val kind = rs.head.myGet("kind")
      val obUrl = g.jsRoutes.controllers.AppController.toIntroduction().url.toString
      val organismB = if (kind == "Himalayan_marmot vs Alpine marmot") {
        a(href := s"${obUrl}#alpine", target := "_blank", "Alpine marmot")
      } else {
        a(href := s"${obUrl}#yellow", target := "_blank", "Yellow-bellied marmot")
      }
      $("#aLocus").html(aLocus)
      $("#bLocus").html(bLocus)
      $("#organismB").html(organismB.render)
      refreshTable(rs)
      plotBlock(rs, aGeneInfo, bGeneInfo)
    }
    $.ajax(ajaxSettings)

  }

  def refreshTable(rs: js.Array[js.Dictionary[js.Any]]) = {
    val columns = js.Array("Gene A", "Locus A", "Gene B", "Locus B", "protein_ID").map { columnName =>
      val field = columnName match {
        case "Gene A" => "aGeneId"
        case "Gene B" => "bGeneId"
        case "protein_ID" => "proteinId"
        case _ => columnName
      }
      val fmt = myFmt(columnName)
      ColumnOptions.title(columnName).field(field).sortable(true).formatter(fmt)
    }
    val array = rs
    val options = TableOptions.columns(columns).data(array)
    $("#table").bootstrapTable("destroy").bootstrapTable(options)

  }

  def plotBlock(rs: js.Array[js.Dictionary[js.Any]], aGeneInfo: GeneInfo, bGeneInfo: GeneInfo) = {
    val rectA = rs.map { x =>
      js.Array(x.myGet("aGeneId"), x.myGet("aStart"), x.myGet("aEnd"))
    }
    val rectB = rs.map { x =>
      js.Array(x.myGet("bGeneId"), x.myGet("bStart"), x.myGet("bEnd"))
    }
    val rate = 0.02
    val length = (aGeneInfo.max - aGeneInfo.min) * rate
    val lengthB = (bGeneInfo.max - bGeneInfo.min) * rate
    val data = js.Dictionary(
      "A" -> js.Dictionary(
        "name" -> aGeneInfo.chr,
        "max" -> (aGeneInfo.max + length),
        "min" -> (aGeneInfo.min - length),
        "rect" -> rectA
      ),
      "B" -> js.Dictionary(
        "name" -> bGeneInfo.chr,
        "max" -> (bGeneInfo.max + lengthB),
        "min" -> (bGeneInfo.min - lengthB),
        "rect" -> rectB
      ),
    )
    val relate = rs.indices.map { i =>
      js.Array(i + 1, i + 1)
    }.toJSArray
    val linkA = g.jsRoutes.controllers.SearchController.getDetailInfo().url
    g.plotBlock(data, relate, linkA)
  }

  def myFmt(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[js.Any]) => columnName match {
    case "Locus A" => {
      s"${row.myGet("aChr")}:${row.myGet("aStart")}-${row.myGet("aEnd")};${row.myGet("aStrand")}"
    }
    case "Locus B" => {
      s"${row.myGet("bChr")}:${row.myGet("bStart")}-${row.myGet("bEnd")};${row.myGet("bStrand")}"
    }
    case "Gene A" => {
      idFmt(v.toString)
    }
    case "protein_ID" => {
      proteinIdFmt(v.toString)
    }

    case _ => v
  }

  def proteinIdFmt(v:String)={
    val hf = s"https://www.ncbi.nlm.nih.gov/protein/${v}.1"
    a(
      target := "_blank",
      href := hf
    )(v).render
  }

  def idFmt(v: String) = {
    val url = g.jsRoutes.controllers.SearchController.getDetailInfo().url
    val hf = s"${url}?geneId=${v}"
    a(
      target := "_blank",
      href := hf
    )(v).render
  }


}
