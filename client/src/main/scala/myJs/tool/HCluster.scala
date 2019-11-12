package myJs.tool

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._
import org.scalajs.dom.{FormData, document}
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, HTMLFormElement, XMLHttpRequest}

import scala.scalajs.js
import scala.scalajs.js.annotation._


@JSExportTopLevel("HCluster")
object HCluster {

  @JSExport("init")
  def init = {

  }

  @JSExport("downloadSvg")
  def downloadSvg = {
    val fileName = "plot.svg"
    val content = $("svg:first").prop("outerHTML").toString
    val blob = new Blob(js.Array(content), BlobPropertyBag(`type` = "image / svg + xml"))
    g.saveAs(blob, fileName)
  }


}
