package myJs

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import com.karasiq.bootstrap.Bootstrap.default._
import org.w3c.dom.html.HTMLElement
import myPkg.Implicits._
import myPkg._
import org.scalajs.dom.raw.{Blob, BlobPropertyBag}

@JSExportTopLevel("HighLights")
object HighLights {

  var tmpData: js.Dictionary[js.Any] = tmpData

  @JSExport("refreshHibernators")
  def refreshHibernators = {
    val url = g.jsRoutes.controllers.HighlightsController.getAllHibernators().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
      val columns = columnNames.map { columnName =>
        val fmt = plagueFmt(columnName)
        js.Dictionary("field" -> columnName, "title" -> columnName, "sortable" -> true, "formatter" -> fmt)
      }.concat(expColumn)
      val dict = js.Dictionary("data" -> rs("array"), "columns" -> columns)
      g.$("#table").bootstrapTable(dict)
    }
    $.ajax(ajaxSettings)

  }

  val expFormatter: js.Function = (v: js.Any, row: js.Dictionary[Any]) => {
    val url = g.jsRoutes.controllers.HighlightsController.expBefore().url.toString
    a(
      target := "_blank",
      href := s"${url}?projectName=${row("Project ID")}"
    )("Show")
  }

  val expColumn = js.Array(js.Dictionary("field" -> "Expression", "title" -> "Expression", "sortable" -> true,
    "formatter" -> expFormatter))

  def expStyle(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[Any]) => columnName match {
    case "geneId"  => v
    case _ => js.Dictionary("css" -> js.Dictionary(
      "padding" -> 0
    ))
  }

  def cellView(json: js.Dictionary[js.Any], row: js.Dictionary[js.Any]) = {
    val logFc = json.myGet("logFc")
    val pValue = json.myGet("pValue")
    val color = json.myGet("color")
    if (logFc == "NA") "None" else {
      val titleString = span()(
        b()("Gene Symbol:"), s" ${row("geneId")}"
      ).render
      val symbolStr = if (row.myGet("symbol") == "NA" || row.myGet("geneId") == "NA") "" else span()(
        b()("Gene Symbol: "), row.myGet("symbol"), br
      ).render
      val contentString = span()(
        raw(symbolStr),
        b()(
          span()(
            "Log",
            sub()(2),
            "-fold change"
          ),
          ":"
        ),
        " ", logFc, br,
        b()("p-Value:"), " ", pValue, br
      ).render
      div(width := "100%",
        height := 36,
        backgroundColor := color,
        title := titleString,
        dataToggle := "popover",
        dataContent := contentString,
        dataContainer := "body",
        dataPlacement := "auto top",
        dataHtml := true,
        dataAnimation := false,
        dataTrigger := "hover")()
    }
  }

  @JSExport("selectAll")
  def selectAll = {
    $(":checkbox").prop("checked", true)
    g.$("#form").bootstrapValidator("revalidateField", "comparisons[]")

  }

  @JSExport("reverseSelect")
  def reverseSelect = {
    $(":checkbox").each { (y: Element, i: Int) =>
      val b = $(y).prop("checked").asInstanceOf[Boolean]
      $(y).prop("checked", !b)
    }
    g.$("#form").bootstrapValidator("revalidateField", "comparisons[]")

  }

  def expFmt(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[js.Any]) => columnName match {
    case "geneId" =>
      val str = v.toString
      span(title := str)(
        str
      )
    case otherName =>
      val json = v.asInstanceOf[js.Dictionary[js.Any]]
      val show = json.myGet("show")
      val html = cellView(json, row)
      if (show == "true") html else div(width := "100%",
        height := 36,
        textAlign.center,
        paddingTop := 8
      )("")
  }

  def pageChange: js.Function = { () =>
    jQuery("[data-toggle='popover']").popover()
  }

  @JSExport("refreshPlague")
  def refreshPlague = {
    val url = g.jsRoutes.controllers.HighlightsController.getAllPlague().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
      val columns: js.Array[js.Dictionary[Any]] = columnNames.map { columnName =>
        val fmt = plagueFmt(columnName)
        js.Dictionary("field" -> columnName, "title" -> columnName, "sortable" -> true, "formatter" -> fmt)
      }.concat(expColumn)
      val dict = js.Dictionary("data" -> rs("array"), "columns" -> columns)
      g.$("#table").bootstrapTable(dict)
    }
    $.ajax(ajaxSettings)
  }

  @JSExport("expSearch")
  def expSearch(showLayer: Boolean = true) = {
    $("#content").hide()
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val index = if (showLayer) layer.alert(element, layerOptions) else 0
      val data = $("#form").serialize()
      val url = g.jsRoutes.controllers.HighlightsController.expSearch().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(data).success { (data, status, e) =>
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        tmpData = rs
        val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
        val columns = columnNames.map { columnName =>
          val style = expStyle(columnName)
          val fmt = expFmt(columnName)
          val title = columnName match {
            case "geneId" => "Gene Symbol"
            case _ => columnName
          }
          ColumnOptions.field(columnName).title(title).sortable(true).titleTooltip(title).formatter(fmt).
            field(columnName).cellStyle(style)
        }
        println(columnNames)
        val options = TableOptions.data(rs("array")).columns(columns).onAll(pageChange)
        $("#table").bootstrapTable("destroy").bootstrapTable(options)
        jQuery("[data-toggle='popover']").popover()
        $("#min").text(rs.myGet("min"))
        $("#max").text(rs.myGet("max"))
        layer.close(index)
        $("#content").show()
      }
      $.ajax(ajaxSettings)

    }

  }

  @JSExport("refreshProjectInfo")
  def refreshProjectInfo = {
    val projectName = $("#projectName").`val`()
    val json = js.Dictionary("projectName" -> projectName)
    val url = g.jsRoutes.controllers.HighlightsController.getProjectInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("post").async(false).data(JSON.stringify(json)).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      $("#projectTitle").text(rs("Project Title").toString)
      $("#organism").text(rs("Organism").toString)
      $("#pubMedId").html(pubmedA(rs("PubMed ID").toString))
      productComparisonsView(rs)

    }
    $.ajax(ajaxSettings)

  }

  @JSExport("download")
  def download = {
    val columnNames = tmpData("columnNames").asInstanceOf[js.Array[String]]
    val header = (js.Array("Gene ID/Gene Symbol") ++ columnNames.drop(1).flatMap { x =>
      js.Array(s"${x}(p-value)", s"${x}(log2FC)")
    }).mkString("\t")
    val array = tmpData("array").asInstanceOf[js.Array[js.Dictionary[js.Any]]]
    val lines = array.map { row =>
      columnNames.flatMap { columnName =>
        columnName match {
          case "geneId" =>
            val geneSymbol = row.myGet("symbol")
            val geneId = row.myGet("geneId")
            val str = if (geneSymbol == "NA") s"${geneId}" else if (geneId == "NA") geneSymbol else
              s"${geneId}/${geneSymbol}"
            js.Array(str)
          case otherName =>
            val json = row(otherName).asInstanceOf[js.Dictionary[js.Any]]
            val logFc = json.myGet("logFc")
            val pValue = json.myGet("pValue")
            if (logFc == "NA") {
              js.Array("NA", "NA")
            } else {
              js.Array(logFc, pValue)
            }
        }
      }.mkString("\t")
    }
    val content = (js.Array(header) ++ lines).mkString("\n")
    downloadTxt(content)

  }

  def downloadTxt(content: String) = {
    val fileName = "out.txt"
    val blob = new Blob(js.Array(content), BlobPropertyBag(`type` = "text/plain;charset=utf-8"))
    g.saveAs(blob, fileName)
  }

  def productComparisonsView(rs: js.Dictionary[js.Any]) = {
    val vsNames = rs("vsNames").asInstanceOf[js.Array[String]]
    val html = vsNames.zipWithIndex.map { case (vsName, i) =>
      label(
        marginRight := 15,
        width := 300,
        cls := "myCheckbox"
      )(input(
        `type` := "checkbox",
        name := "comparisons[]",
        value := vsName,
        if (i < 6) checked else raw("")
      )(vsName)
      )
    }.mkString
    $("#comparisons").empty().html(html)

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
        "pMin" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "numeric" -> js.Dictionary(
              "message" -> "P-Value min value  must be numeric!"
            )
          )
        ),
        "pMax" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "numeric" -> js.Dictionary(
              "message" -> "P-Value max value  must be numeric!"
            )
          )
        ),
        "min" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "numeric" -> js.Dictionary(
              "message" -> "Log2-fold change min value  must be numeric!"
            )
          )
        ),
        "max" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "numeric" -> js.Dictionary(
              "message" -> "Log2-fold change max value  must be numeric!"
            )
          )
        ),
        "comparisons[]" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "choice" -> js.Dictionary(
              "message" -> "The number of comparisons must not less than 1！",
              "min" -> 1
            )
          )
        ),
      )
    )
    g.$("#form").bootstrapValidator(dict)

  }

  def pubmedA(v: String) = if (v == "NA") "NA" else a(
    target := "_blank",
    href := s"https://www.ncbi.nlm.nih.gov/pubmed/${v}"
  )(v).render

  def plagueFmt(columnName: String): js.Function = (v: String) => columnName match {
    case "PubMed ID" => pubmedA(v)
    case "Project ID" => {
      val showId = v.split("-")(0)
      a(
        target := "_blank",
        href := s"https://www.ncbi.nlm.nih.gov/bioproject/?term=${showId}"
      )(showId)
    }
    case _ => v
  }


}
