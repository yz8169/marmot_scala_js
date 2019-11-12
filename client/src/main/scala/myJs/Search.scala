package myJs

import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.annotation._
import Utils._
import org.scalajs.dom.Element
import com.karasiq.bootstrap.Bootstrap.default._
import myJs.myPkg.{ColumnOptions, TableOptions, TypeaheadOptions}
import myPkg.Implicits._
import shared.Pojo.ChrData

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{JSON, URIUtils}


@JSExportTopLevel("Search")
object Search {

  val regionFormId = "regionForm"

  @JSExport("init")
  def init = {
    produceSpeciesView
    val geneId = $("#myGeneId").`val`().toString.trim
    if (geneId != "") {
      $(":input[name='keyword']").`val`(geneId)
      keywordSearch
    }
    refreshChrTypeahead
  }

  @JSExport("geneIdExampleChange")
  def geneIdExampleChange(y: Element) = {
    val value = $(y).`val`().toString
    val egStr = value match {
      case "Himalayan marmot" =>
        "Mar00001,Mar00004,Mar00008"
      case "Yellow-bellied marmot" =>
        "XP_027781386,XP_027781387,XP_027781388"
      case "Alpine marmot" =>
        "XP_015331352,XP_015331354,XP_015331353"
    }
    $("#egGeneId").text(egStr)
  }

  @JSExport("geneNameExampleChange")
  def geneNameExampleChange(y: Element) = {
    val value = $(y).`val`().toString
    val egStr = value match {
      case "Himalayan marmot" =>
        "Ankrd45,Dnah1,Foxd2"
      case "Yellow-bellied marmot" =>
        "LOC114082670,Mki67,Kcnk18"
      case "Alpine marmot" =>
        "LOC107133529,Ubqln3,Ubqlnl"
    }
    $("#egGeneName").text(egStr)
  }

  @JSExport("regionExampleChange")
  def regionExampleChange(y: Element) = {
    val value = $(y).`val`().toString
    val chrData = value match {
      case "Himalayan marmot" =>
        ChrData("scaffold1", 15, 2000000)
      case "Yellow-bellied marmot" =>
        ChrData("NW_020981937.1", 3000, 50000)
      case "Alpine marmot" =>
        ChrData("NW_015351472.1", 5000, 200000)
    }
    $("#egChr").text(chrData.chr)
    $("#egStart").text(chrData.start.toString)
    $("#egEnd").text(chrData.end.toString)
    refreshChrTypeahead
  }

  def produceSpeciesView = {
    produceGeneIdSpeciesView
    produceGeneNameSpeciesView
    produceRegionSpeciesView
  }

  def produceGeneIdSpeciesView = {
    val formId = "geneIdForm"
    val names = Tool.species
    val html = names.zipWithIndex.map { case (vsName, i) =>
      label(
        marginRight := 15,
        width := 200,
      )(input(
        `type` := "radio",
        name := "species",
        value := vsName,
        if (i == 0) checked else raw(""),
        onchange := s"Search.geneIdExampleChange(this)"
      )(vsName)
      )
    }.mkString
    $(s"#${formId} #species").empty().html(html)
  }

  def produceGeneNameSpeciesView = {
    val formId = "geneNameForm"
    val names = Tool.species
    val html = names.zipWithIndex.map { case (vsName, i) =>
      label(
        marginRight := 15,
        width := 200,
      )(input(
        `type` := "radio",
        name := "species",
        value := vsName,
        if (i == 0) checked else raw(""),
        onchange := s"Search.geneNameExampleChange(this)"
      )(vsName)
      )
    }.mkString
    $(s"#${formId} #species").empty().html(html)
  }

  def produceRegionSpeciesView = {
    val formId = regionFormId
    val names = Tool.species
    val html = names.zipWithIndex.map { case (vsName, i) =>
      label(
        marginRight := 15,
        width := 200,
      )(input(
        `type` := "radio",
        name := "species",
        value := vsName,
        if (i == 0) checked else raw(""),
        onchange := s"Search.regionExampleChange(this)"
      )(vsName)
      )
    }.mkString
    $(s"#${formId} #species").empty().html(html)
  }

  @JSExport("geneIdFmt")
  def geneIdFmt: js.Function = {
    (v: String, row: js.Dictionary[js.Any]) =>
      val url = g.jsRoutes.controllers.SearchController.getDetailInfo().url
      val hf = s"${url}?geneId=${v}"
      a(
        target := "_blank",
        href := hf
      )(v)
  }

  def refreshChrTypeahead = {
    val formId = regionFormId
    val species = $(s"#${formId} :input[name='species']:checked").`val`().toString
    val url = g.jsRoutes.controllers.SearchController.getAllChrs().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?species=${species}").contentType("application/json").
      `type`("get").async(false).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[String]]
      $("#chr").typeahead("destroy").typeahead(TypeaheadOptions.source(rs))
    }
    $.ajax(ajaxSettings)
  }


  @JSExport("mySearch")
  def mySearch = {
    val data = $("#form").serialize()
    val url = g.jsRoutes.controllers.BrowseController.getGo().url.toString
    val dict = js.Dictionary("skin" -> "layui-layer-molv",
      "closeBtn" -> 0,
      "title" -> "Info",
      "btn" -> js.Array(),
    )
    val element = div()(
      span()("Running..."),
      " ",
      img(src := "/assets/images/running2.gif",
        width := 30,
        height := 20
      )()
    ).render
    val index = g.layer.alert(element, dict)
    val ajaxSettings = JQueryAjaxSettings.url(url).
      `type`("post").data(data).success { (data, status, e) =>
      g.layer.close(index)
      g.$("#table").bootstrapTable("load", data)
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("showExample")
  def showExample(y: Element, name: String) = {
    val example = $(y).find("em").text().trim
    $(s"textarea[name='${name}']").`val`(example)
    g.$(s"#${name}Form").formValidation("revalidateField", name)
  }

  @JSExport("keywordSearch")
  def keywordSearch = {
    val formId = "#keywordForm"
    val bv = jQuery(formId).data("formValidation")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = js.Dictionary(
        "keyword" -> $(":input[name='keyword']").`val`().toString
      )
      val index = layer.alert(element, layerOptions)
      val url = g.jsRoutes.controllers.SearchController.searchByKeyword().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(JSON.stringify(data)).contentType("application/json").success { (data, status, e) =>
        g.layer.close(index)
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
        val columns = columnNames.map { columnName =>
          val title = columnName match {
            case "id" => "ID"
            case "match" => "Match"
            case _ => columnName
          }
          val fmt = myFmt(columnName)
          ColumnOptions.field(columnName).title(title).sortable(true).titleTooltip(columnName).formatter(fmt)
        }
        val array = rs("array").asInstanceOf[js.Array[js.Dictionary[js.Any]]]
        val newArray = array.map { row =>
          val infos = row("match").asInstanceOf[js.Dictionary[String]]
          val revInfos = Tool.revDict(infos)
          val info = revInfos.map { case (value, keys) =>
            keys.fold(value) { (value, key) =>
              val index = value.toUpperCase.indexOfSlice(key.toUpperCase)
              val reValue = value.slice(index, index + key.size)
              value.replace(reValue, s"<span style='color:red'>${reValue}</span>")
            }
          }.mkString(" | ")
          js.Dictionary("id" -> row.myGet("id"), "match" -> info)
        }.sortBy(x => x("id"))
        val options = TableOptions.data(newArray).columns(columns)
        $("#table").bootstrapTable("destroy").bootstrapTable(options)
        $("#result").show()
      }
      $.ajax(ajaxSettings)
    }

  }

  def myFmt(columnName: String): js.Function = (v: String, row: js.Dictionary[js.Any]) => columnName match {
    case "id" =>
      val url = g.jsRoutes.controllers.SearchController.getDetailInfo().url
      val hf = s"${url}?geneId=${v}"
      a(
        target := "_blank",
        href := hf
      )(v).render
    case "chr" =>
      row("chr") + ":" + row("start") + "-" + row("end")
    case _ => v
  }

  @JSExport("geneIdSearch")
  def geneIdSearch = {
    val formId = "#geneIdForm"
    val bv = jQuery(formId).data("formValidation")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $(formId).serialize()
      val index = layer.alert(element, layerOptions)
      val url = g.jsRoutes.controllers.SearchController.searchByGeneId().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        g.layer.close(index)
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        val columns = myColumns
        val array = rs.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
        val options = TableOptions.data(array).columns(columns)
        $("#table").bootstrapTable("destroy").bootstrapTable(options)
        $("#result").show()
      }
      $.ajax(ajaxSettings)
    }

  }

  def myColumns = {
    val columnNames = js.Array("id", "symbol", "description", "chr", "strand")
    val thNames = js.Array("ID", "Symbol", "Description", "Locus", "Strand")
    columnNames.zip(thNames).map { case (fieldName, thName) =>
      val fmt = myFmt(fieldName)
      ColumnOptions.field(fieldName).title(thName).sortable(true).titleTooltip(fieldName).formatter(fmt)
    }
  }

  @JSExport("geneNameSearch")
  def geneNameSearch = {
    val formId = "#geneNameForm"
    val bv = jQuery(formId).data("formValidation")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $(formId).serialize()
      val index = layer.alert(element, layerOptions)
      val url = g.jsRoutes.controllers.SearchController.searchByGeneName().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        g.layer.close(index)
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        val columns = myColumns
        val array = rs.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
        val options = TableOptions.data(array).columns(columns)
        $("#table").bootstrapTable("destroy").bootstrapTable(options)
        $("#result").show()
      }
      $.ajax(ajaxSettings)
    }

  }

  @JSExport("regionSearch")
  def regionSearch = {
    val formId = "#regionForm"
    val bv = jQuery(formId).data("formValidation")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $(formId).serialize()
      val index = layer.alert(element, layerOptions)
      val url = g.jsRoutes.controllers.SearchController.searchByRegion().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        g.layer.close(index)
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        val columns = myColumns
        val array = rs.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
        val options = TableOptions.data(array).columns(columns)
        $("#table").bootstrapTable("destroy").bootstrapTable(options)
        $("#result").show()
      }
      $.ajax(ajaxSettings)
    }

  }


}
