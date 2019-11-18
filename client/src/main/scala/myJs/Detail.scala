package myJs

import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import Utils._
import shared.SharedTool

@JSExportTopLevel("Detail")
object Detail {
  var geneId = ""

  @JSExport("init")
  def init(id: String) = {
    geneId = id
    mySearch(false)
    refreshGo
    refreshMyKegg
    refreshOrthologs
    refreshSynteny
    refreshDeg
    refreshBySpecies

  }

  def refreshBySpecies = {
    val url = g.jsRoutes.controllers.SearchController.getBasicInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?geneId=${geneId}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val species = rs.myGet("species")
      val loc = s"${rs.myGet("chr")}:${rs.myGet("start")}..${rs.myGet("end")}"
      val src = species match {
        case SharedTool.himalayanName =>
          s"http://47.88.57.209:8080/jb/index.html?data=marmot/data&loc=${loc}&tracks=<span style='display:none;'>1</span>Reference sequence ,<span style='display:none;'>3</span>Gene Name&tracklist=0&nav=1&overview=0"
        case SharedTool.alpineName =>
          s"http://47.88.57.209:8080/jb/index.html?data=marmot/alpine_marmot&loc=${loc}&tracks=DNA,Annotation&tracklist=0&nav=1&overview=0"
        case SharedTool.yellowName =>
          s"http://47.88.57.209:8080/jb/index.html?data=marmot/yellow_marmot&loc=${loc}&tracks=DNA,Annotation&tracklist=0&nav=1&overview=0"
      }
      $("#jBrowse").attr("src", src)
      if (species != SharedTool.himalayanName) {
        $("#geneExp").hide()
        $("#deg").hide()
        $("#syntenicBlock").hide()
      }


    }
    $.ajax(ajaxSettings)

  }

  def refreshGo = {
    val url = g.jsRoutes.controllers.SearchController.getAllGeneGo().url.toString
    val json = js.Dictionary("geneId" -> geneId)
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("post").data(JSON.stringify(json)).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val head = rs.myHead
      val content = head.myGet("content")
      val dict = JSON.parse(content).asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val terms = dict.map { x =>
        span(
          a(
            href := s"http://amigo.geneontology.org/amigo/term/${x("term")}",
            target := "_blank"
          )(x("term").toString),
          raw("&nbsp;&nbsp;"),
          x("description").toString,
          raw("&nbsp;&nbsp;"),
          x("category").toString,
          br
        ).render
      }.mkString
      $("#terms").html(terms)

    }
    $.ajax(ajaxSettings)

  }

  def refreshMyKegg = {
    val url = g.jsRoutes.controllers.SearchController.getAllGeneKegg().url.toString
    val json = js.Dictionary("geneId" -> geneId)
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("post").data(JSON.stringify(json)).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val head = rs.myHead
      val termHtml = if (head.myGet("ko") == "NA") "NA" else a(
        href := s"http://www.kegg.jp/dbget-bin/www_bget?ko:${head.myGet("ko")}",
        target := "_blank"
      )(head.myGet("ko")).render
      $("#keggTerm").html(termHtml)
      $("#keggDescription").html(head.myGet("description"))
    }
    $.ajax(ajaxSettings)

  }

  def refreshOrthologs = {
    val url = g.jsRoutes.controllers.SearchController.getAllOrthologs().url.toString
    val json = js.Dictionary("geneId" -> geneId)
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("post").data(JSON.stringify(json)).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val array = Array("human", "mouse", "rat", "hamster", "blackBear", "squirrel", "bat")
      val shows = Array("Human", "Mouse", "Rat", "Hamster", "Black bear", "Squirrel", "Little brown bat")
      val speciesDict = js.Dictionary("Human" -> "Homo_sapiens", "Mouse" -> "Mus_musculus", "Rat" -> "Rattus_norvegicus",
        "Hamster" -> "Mesocricetus_auratus", "Black bear" -> "Ursus_americanus", "Squirrel" -> "Ictidomys_tridecemlineatus",
        "Little brown bat" -> "Myotis_lucifugus")
      val html = shows.zipWithIndex.map { case (x, i) =>
        val id = rs.myGet(array(i))
        if (id == "NA") {
          x
        } else {
          a(href := s"https://ensembl.org/${speciesDict(x)}/Gene/Summary?g=${id}", target := "_blank", x).render
        }
      }.mkString("; ")

      val otherDict = js.Dictionary(
        "Yellow-bellied marmot" -> "yellow",
        "Alpine marmot" -> "alpine",
        "Himalayan marmot" -> "himalayan"
      )
      val otherHtml = otherDict.filter { case (x, key) =>
        val id = rs.myGet(key)
        geneId != id
      }.map { case (x, key) =>
        val id = rs.myGet(key)
        if (id == "NA") {
          x
        } else {
          a(href := s"https://www.ncbi.nlm.nih.gov/protein/${id}.1", target := "_blank", x).render
        }
      }.mkString("; ")
      val finalHtml = s"${html}; ${otherHtml}"
      $("#orthologs").html(finalHtml)
    }
    $.ajax(ajaxSettings)

  }

  def refreshDeg = {
    val url = g.jsRoutes.controllers.SearchController.getDegInfo().url.toString
    val json = js.Dictionary("geneId" -> geneId)
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("post").data(JSON.stringify(json)).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[String]]
      rs.foreach { case (id, v) =>
        $(s"td[id='${id}']").html(v)
      }
    }
    $.ajax(ajaxSettings)

  }


  def refreshSynteny = {
    val url = g.jsRoutes.controllers.GeneBlockController.getGeneBlock().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?geneId=${geneId}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[js.Any]]]
      val infos = rs.map { x =>
        val kind = x.myGet("kind")
        val block = x.myGet("blockName")
        (kind, block)
      }.distinct.groupBy(_._1).mapValues(_.map(_._2))
      infos.foreach { case (kind, blocks) =>
        val blockHtml = blocks.map { x =>
          getBlockA(x)
        }.mkString("<br>")
        $(s"td[id='${kind}']").html(blockHtml)
      }
    }
    $.ajax(ajaxSettings)

  }

  def getBlockA(x: String) = {
    val url = g.jsRoutes.controllers.GeneBlockController.blockInfoBefore().url.toString
    a(href := s"${url}?blockId=${x}", target := "_blank", x).render
  }

  @JSExport("mySearch")
  def mySearch(showLayer: Boolean = true) = {
    $("#charts").html("<img src='/assets/images/loading.gif'/>");
    val data = $("#form").serialize()
    val url = g.jsRoutes.controllers.SearchController.boxPlotByGeneId().url.toString

    val index = if (showLayer) layer.alert(element, layerOptions) else 0
    val ajaxSettings = JQueryAjaxSettings.url(url).
      `type`("get").data(data).success { (data, status, e) =>
      g.boxPlot(data)
      layer.close(index)
    }
    $.ajax(ajaxSettings)
    val loadingImage = img(src := "/assets/images/loading.gif").render
    $("#charts1").html(loadingImage)
    val barPloturl = g.jsRoutes.controllers.SearchController.barPlot().url.toString
    val barPlotAjaxSettings = JQueryAjaxSettings.url(barPloturl).
      `type`("get").data(data).success { (data, status, e) =>
      val json = data.asInstanceOf[js.Dictionary[js.Any]]
      if (json.myGet("valid") == "false") {
        $("#charts1").html("")
        //        g.swal("Error", json("message").toString, "error")
      } else {
        $("#charts1").html(json("div").toString)
      }
    }
    $.ajax(barPlotAjaxSettings)


  }


}
