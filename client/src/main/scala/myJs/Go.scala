package myJs

import org.querki.jquery._

import scala.scalajs.js.annotation._
import scala.scalajs.js
import scala.scalajs.js.{JSON, URIUtils}
import scalatags.Text.all._
import Utils._
import myJs.myPkg.LayerOptions
import com.karasiq.bootstrap.Bootstrap.default._

import scala.collection.mutable.ArrayBuffer


@JSExportTopLevel("Go")
object Go {

  @JSExport("init")
  def init = {
    g.$("#table").bootstrapTable({})
    productView
    bootStrapValidator

  }

  def productView = {
    val url = g.jsRoutes.controllers.BrowseController.getGoInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("get").async(false).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      produceKindsView(rs)
      produceSpeciesView(rs)
    }
    $.ajax(ajaxSettings)

  }

  def produceKindsView(rs: js.Dictionary[js.Any]) = {
    val names = rs("kinds").asInstanceOf[js.Array[String]]
    val html = names.zipWithIndex.map { case (vsName, i) =>
      label(
        marginRight := 15,
        width := 300
      )(input(
        `type` := "checkbox",
        name := "kinds[]",
        value := vsName,
        checked
      )(vsName)
      )
    }.mkString
    $("#kinds").empty().html(html)

  }

  def produceSpeciesView(rs: js.Dictionary[js.Any]) = {
    val names = rs("species").asInstanceOf[js.Array[String]]
    val sortNames=ArrayBuffer("Himalayan marmot")++(names-"Himalayan marmot")
    val html = sortNames.zipWithIndex.map { case (vsName, i) =>
      label(
        marginRight := 15,
        width := 300,
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

  @JSExport("goFmt")
  def goFmt: js.Function = {
    (v: String) =>
      a(
        href := s"http://amigo.geneontology.org/amigo/term/${v}",
        target := "_blank"
      )(v).render
  }

  @JSExport("genesFmt")
  def genesFmt: js.Function = {
    (v: String, row: js.Dictionary[js.Any]) =>
      if(row.myGet("species")=="Himalayan marmot"){
        val url = g.jsRoutes.controllers.SearchController.getDetailInfo().url
        val columns = v.split(";").map { x =>
          val hf = s"${url}?geneId=${x}"
          a(
            target := "_blank",
            href := hf
          )(x)
        }
        columns.mkString(" ")
      }else {
        val columns = v.split(";").map { x =>
          BlockInfo.proteinIdFmt(x)
        }
        columns.mkString(" ")
      }


  }

  @JSExport("mySearch")
  def mySearch = {
    $("#content").hide()
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $("#form").serialize()
      val url = g.jsRoutes.controllers.BrowseController.getGo().url.toString
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
        "kinds[]" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "choice" -> js.Dictionary(
              "message" -> "The number of category must not less than 1！",
              "min" -> 1
            )
          )
        ),
      )
    )
    g.$("#form").bootstrapValidator(dict)

  }


}
