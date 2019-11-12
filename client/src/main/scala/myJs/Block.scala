package myJs

import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg.{ColumnOptions, TableOptions}
import org.querki.jquery._
import scalatags.Text.all._

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation._
import com.karasiq.bootstrap.Bootstrap.default._


@JSExportTopLevel("Block")
object Block {

  @JSExport("init")
  def init = {
    productView

  }

  def productView = {
    val url = g.jsRoutes.controllers.GeneBlockController.getKinds().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("get").async(false).success { (data, status, e) =>
      val names = data.asInstanceOf[js.Array[String]]
      val html = names.zipWithIndex.map { case (vsName, i) =>
        label(
          marginRight := 15,
          width := 400,
        )(input(
          `type` := "radio",
          name := "kinds[]",
          value := vsName,
          if (i == 0) checked else raw(""),
        )(vsName)
        )
      }.mkString
      $("#kinds").empty().html(html)

    }
    $.ajax(ajaxSettings)

  }

  @JSExport("mySearch")
  def mySearch = {
    $("#content").hide()
    val data = $("#form").serialize()
    val url = g.jsRoutes.controllers.GeneBlockController.getBlocks().url.toString
    val index = layer.alert(element, layerOptions)
    val ajaxSettings = JQueryAjaxSettings.url(url).
      `type`("post").data(data).success { (data, status, e) =>
      layer.close(index)
      val rs = data.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val columns = js.Array("Block ID", "Location A", "Location B").map { columnName =>
        val field = columnName match {
          case "Block ID" => "blockId"
          case "Location A" => "locationA"
          case "Location B" => "locationB"
          case _ => columnName
        }
        val fmt = myFmt(columnName)
        ColumnOptions.title(columnName).field(field).sortable(true).formatter(fmt)
      }
      val array = rs
      val options = TableOptions.columns(columns).data(array)
      $("#table").bootstrapTable("destroy").bootstrapTable(options)
      $("#content").show()
    }
    $.ajax(ajaxSettings)

  }

  def myFmt(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[js.Any]) => columnName match {
    case "Block ID" =>
     Detail.getBlockA(v.toString)
    case _ => v
  }


}
