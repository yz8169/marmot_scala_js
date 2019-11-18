package myJs

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._
import shared.SharedTool

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import com.karasiq.bootstrap.Bootstrap.default._
import myJs.myPkg.{Swal, SwalOptions}


@JSExportTopLevel("Faq")
object Faq {

  @JSExport("init")
  def init = {
    bootStrapValidator

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
        "question" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "question is required！",
            )
          )
        ),
        "email" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "email is required！",
            ),
            "emailAddress" -> js.Dictionary(
              "message" -> "email is invalid！",
            ),
          )
        ),
      )
    )
    g.$("#form").bootstrapValidator(dict)

  }

  @JSExport("mySearch")
  def mySearch = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $("#form").serialize()
      val url = g.jsRoutes.controllers.AppController.addQuestion().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        Swal.swal(SwalOptions.title("Success").text("Submit successfully!").`type`("success"))
      }
      $.ajax(ajaxSettings)
    }


  }


}
