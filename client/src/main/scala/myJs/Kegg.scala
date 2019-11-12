package myJs

import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import Utils._
import com.karasiq.bootstrap.Bootstrap.default._

import scala.collection.mutable.ArrayBuffer



@JSExportTopLevel("Kegg")
object Kegg {

  @JSExport("init")
  def init = {
    g.$("#table").bootstrapTable({})
    productSpeciesView
    bootStrapValidator

  }

  @JSExport("mySearch")
  def mySearch = {
    $("#content").hide()
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $("#form").serialize()
      val url = g.jsRoutes.controllers.BrowseController.getAllKegg().url.toString
      val index = layer.alert(element, layerOptions)
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        g.layer.close(index)
        g.$("#table").bootstrapTable("load", data)
        $("#content").show()
      }
      $.ajax(ajaxSettings)
    }


  }

  @JSExport("bootStrapValidator")
  def bootStrapValidator = {
    val dict = js.Dictionary(
      "feedbackIcons" -> js.Dictionary(
        "valid" -> "glyphicon glyphicon-ok",
        "invalid" -> "glyphicon glyphicon-remove",
        "validating" -> "glyphicon glyphicon-refresh",
      ),
      "fields" -> js.Dictionary(
      )
    )
    g.$("#form").bootstrapValidator(dict)

  }

  def productSpeciesView = {
    val url = g.jsRoutes.controllers.BrowseController.getKeggSpecies().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("get").async(false).success { (data, status, e) =>
      val names = data.asInstanceOf[js.Array[String]]
      val sortNames=ArrayBuffer("Himalayan marmot")++(names-"Himalayan marmot")
      val html = sortNames.zipWithIndex.map { case (vsName, i) =>
        label(
          marginRight := 15,
          width := 300
        )(input(
          `type` := "radio",
          name := "species[]",
          value := vsName,
          if(i==0) checked else raw(""),
        )(vsName)
        )
      }.mkString
      $("#species").empty().html(html)

    }
    $.ajax(ajaxSettings)

  }

  @JSExport("genesFmt")
  def genesFmt: js.Function = {
    (v: String, row: js.Dictionary[js.Any]) =>
      if(row.myGet("kind")=="Himalayan marmot"){
        val url = g.jsRoutes.controllers.SearchController.getDetailInfo().url
        val columns = v.split(";").map { x =>
          val hf = s"${url}?geneId=${x}"
          a(
            target := "_blank",
            href := hf
          )(x)
        }
        columns.mkString(" ")
      }else{
        val columns = v.split(";").map { x =>
          BlockInfo.proteinIdFmt(x)
        }
        columns.mkString(" ")
      }


  }


}
