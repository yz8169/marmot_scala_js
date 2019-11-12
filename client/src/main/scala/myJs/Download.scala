package myJs

import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.annotation._
import Utils._


@JSExportTopLevel("Download")
object Download {

  @JSExport("init")
  def init = {
    g.$("#snpTable").bootstrapTable()
    refreshGenomeData

  }

  def refreshGenomeData = {
    val url = g.jsRoutes.controllers.DownloadController.getGenomeData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
      val columns = columnNames.map { columnName =>
        val fmt = downloadFmt(columnName)
        js.Dictionary("field" -> columnName, "title" -> columnName, "sortable" -> true, "formatter" -> fmt)
      }
      val dict = js.Dictionary("data" -> rs("array"), "columns" -> columns)
      g.$("#table").bootstrapTable(dict)
    }
    $.ajax(ajaxSettings)

  }

  def downloadFmt(columnName: String): js.Function = (v: String) => columnName match {
    case "File name" => {
      val url = g.jsRoutes.controllers.DownloadController.downloadData().url.toString
      a(
        href := s"${url}?fileName=${v}",
        v
      )
    }
    case _ => v
  }


}
