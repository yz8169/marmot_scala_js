package myJs

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.annotation._

@JSExportTopLevel("HibernateDiffGene")
object HibernateDiffGene {

  @JSExport("init")
  def init = {
    refreshSpecies
    refreshPlatform
    refreshTissue

  }

  def refreshSpecies = {
    val url = g.jsRoutes.controllers.BrowseController.getAllSpecies().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).
      `type`("get").success { (data, status, e) =>
      val dict = js.Dictionary(
        "placeholder" -> "click to select",
        "allowClear" -> true,
        "data" -> data,
      )
      g.$(".species").select2(dict).`val`("").trigger("change")
    }
    $.ajax(ajaxSettings)
  }

  def refreshPlatform = {
    val url = g.jsRoutes.controllers.BrowseController.getAllPlatform().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).
      `type`("get").success { (data, status, e) =>
      val dict = js.Dictionary(
        "placeholder" -> "click to select",
        "allowClear" -> true,
        "data" -> data,
      )
      g.$(".platform").select2(dict).`val`("").trigger("change")
    }
    $.ajax(ajaxSettings)
  }

  def refreshTissue = {
    val url = g.jsRoutes.controllers.BrowseController.getAllTissue().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).
      `type`("get").success { (data, status, e) =>
      val dict = js.Dictionary(
        "placeholder" -> "click to select",
        "allowClear" -> true,
        "data" -> data,
      )
      g.$(".tissue").select2(dict).`val`("").trigger("change")
    }
    $.ajax(ajaxSettings)
  }


  @JSExport("goFmt")
  def goFmt: js.Function = {
    (v: String) =>
      a(
        href := s"http://amigo.geneontology.org/amigo/term/${v}",
        target := "_blank"
      )(v)
  }

  @JSExport("genesFmt")
  def genesFmt: js.Function = {
    (v: String) =>
      val url = g.jsRoutes.controllers.SearchController.getDetailInfo().url
      val columns = v.split(";").map { x =>
        val hf = s"${url}?geneId=${x}"
        a(
          target := "_blank",
          href := hf
        )(x)
      }
      columns.mkString(" ")

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


}
