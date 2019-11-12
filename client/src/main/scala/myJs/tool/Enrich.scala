package myJs.tool

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.BlockInfo
import org.querki.jquery._
import scalatags.Text.all._

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.annotation._
import myJs.Utils._
import myJs.myPkg._
import myJs.myPkg.Implicits._
import org.scalajs.dom.FormData
import org.scalajs.dom.document
import org.scalajs.dom.raw.{HTMLFormElement, XMLHttpRequest}

import scala.scalajs.js.JSON
import scalajs.js.JSConverters._


@JSExportTopLevel("Enrich")
object Enrich {

  @JSExport("init")
  def init = {
    inputCheck("keggForm")

  }

  @JSExport("change")
  def change(y: Element, formId: String) = {
    val value = $(y).find(">option:selected").`val`().toString
    val egStr = value match {
      case "human" =>
        "ENSG00000206474,ENSG00000221996,ENSG00000172208"
      case "rat" =>
        "ENSRNOG00000040300,ENSRNOG00000058808,ENSRNOG00000061316,ENSRNOG00000050129"
      case "mouse" =>
        "ENSMUSG00000000001,ENSMUSG00000000125,ENSMUSG00000000126,ENSMUSG00000000127"
      case "rabbit" =>
        "ENSOCUP00000009409.2,ENSOCUP00000002911.2,ENSOCUP00000002121.2,ENSOCUP00000026235.1"
      case "marmot" =>
        "Mar00001,Mar00004,Mar00008"
      case "yellow" =>
        "XP_027781386,XP_027781387,XP_027781388"
      case "alpine" =>
        "XP_015331352,XP_015331354,XP_015331353"
    }
    $("#" + formId).find("#egGeneId").text(egStr)
  }

  @JSExport("goEnrich")
  def goEnrich = {
    val formId = "keggForm"
    var form = $(s"#${formId}")
    if (inputCheck(formId)) {
      val formData = new FormData(document.getElementById(formId).asInstanceOf[HTMLFormElement])
      val index = layer.alert(element, layerOptions)
      val url = g.jsRoutes.controllers.ToolController.goEnrich().url.toString
      val xhr = new XMLHttpRequest
      xhr.open("post", url)
      xhr.responseType = "json"
      xhr.onreadystatechange = (e) => {
        if (xhr.readyState == XMLHttpRequest.DONE) {
          val data = xhr.response
          val rs = data.asInstanceOf[js.Dictionary[js.Any]]
          layer.close(index)
          if (rs.myGet("valid") == "false") {
            Swal.swal(SwalOptions.title("Error").text(rs("message")).`type`("error"))
            $("#goResult").hide()
          } else {
            $("#goResult #table").bootstrapTable("load", data)
            $("#keggResult").hide()
            $("#goResult").show()
          }
        }
      }
      xhr.send(formData)
    }

  }

  @JSExport("keggEnrich")
  def keggEnrich = {
    val formId = "keggForm"
    var form = $(s"#${formId}")
    val resultId = "keggResult"
    val bv = jQuery(s"#${formId}").data("formValidation")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid && inputCheck(formId)) {
      val formData = new FormData(document.getElementById(formId).asInstanceOf[HTMLFormElement])
      val index = layer.alert(element, layerOptions)
      val url = g.jsRoutes.controllers.ToolController.keggEnrich().url.toString
      val xhr = new XMLHttpRequest
      xhr.open("post", url)
      xhr.responseType = "json"
      xhr.onreadystatechange = (e) => {
        if (xhr.readyState == XMLHttpRequest.DONE) {
          val data = xhr.response
          val rs = data.asInstanceOf[js.Dictionary[js.Any]]
          layer.close(index)
          if (rs.myGet("valid") == "false") {
            Swal.swal(SwalOptions.title("Error").text(rs("message")).`type`("error"))
            $(s"#${resultId}").hide()
          } else {
            $(s"#${resultId} #table").bootstrapTable("load", data)
            $("#keggResult").hide()
            $(s"#${resultId}").show()
          }
        }
      }
      xhr.send(formData)

    }

  }

  @JSExport("mySearch")
  def mySearch = {
    val value = $(":input[name='anaMethod']").`val`
    if (value == "kegg") {
      keggEnrich
    } else {
      goEnrich
    }

  }

  def inputCheck(formId: String) = {
    val text = $(s"#${formId} #geneId").`val`.toString
    val file = $(s"#${formId} #input-1").`val`.toString
    if (file.nonEmpty && text.nonEmpty) {
      Swal.swal(SwalOptions.title("Error").text("Please either enter gene id into the box or upload a file, not both!").
        `type`("error"))
      false
    } else if (file.isEmpty && text.isEmpty) {
      Swal.swal(SwalOptions.title("Error").text("Please enter gene ID or upload file!").
        `type`("error"))
      false
    } else true

  }


}
