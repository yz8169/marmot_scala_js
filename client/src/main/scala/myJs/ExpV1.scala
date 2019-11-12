package myJs

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._

@JSExportTopLevel("ExpV1")
object ExpV1 {


  @JSExport("init")
  def init = {
    refreshTissue
  }

  @JSExport("change")
  def change(y: Element) = {
    val value = $(y).find(">option:selected").`val`()
    refreshProjectInfo
    HighLights.expSearch(true)
  }

  def refreshTissue = {
    val projectName = $("#projectName").`val`()
    val url = g.jsRoutes.controllers.HighlightsController.getTissues().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?projectName=${projectName}").async(false).success { (data, status, e) =>
      val tissues = data.asInstanceOf[js.Array[String]]
      val html = tissues.map { x =>
        option(value := x, x)
      }.mkString
      $("select[name='tissue']").html(html)

    }
    $.ajax(ajaxSettings)


  }

  @JSExport("refreshProjectInfo")
  def refreshProjectInfo = {
    val projectName = $("#projectName").`val`()
    val json = js.Dictionary(
      "projectName" -> projectName,
      "tissue" -> $("select[name='tissue']").`val`(),
    )
    val url = g.jsRoutes.controllers.HighlightsController.getProjectInfoV1().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("post").async(false).data(JSON.stringify(json)).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      $("#projectTitle").text(rs("Project Title").toString)
      $("#organism").text(rs("Organism").toString)
      $("#pubMedId").html(HighLights.pubmedA(rs("PubMed ID").toString))
      HighLights.productComparisonsView(rs)
    }
    $.ajax(ajaxSettings)

  }


}
