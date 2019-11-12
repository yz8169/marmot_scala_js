package controllers

import java.io.{File, FilenameFilter}

import javax.inject.Inject
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import tool.{FormTool, Tool}
import utils.Utils
import utils.Implicits._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.math.BigDecimal.RoundingMode
import scala.collection.JavaConverters

/**
  * Created by yz on 2019/2/27
  */
class HighlightsController @Inject()(cc: ControllerComponents,
                                     formTool: FormTool, tool: Tool) extends AbstractController(cc) {

  def marmotSBefore = Action { implicit request =>
    Ok(views.html.highlights.marmotS())
  }

  def hibernatorsBefore = Action { implicit request =>
    Ok(views.html.highlights.hibernators())
  }

  def getAllHibernators = Action { implicit request =>
    val file = new File(Utils.dataDir, "附件5.2.xlsx")
    val (columnNames, array) = Utils.getInfoByXlsxFile(file)
    val json = Json.obj("columnNames" -> columnNames, "array" -> array)
    Ok(json)
  }

  def plagueBefore = Action { implicit request =>
    Ok(views.html.highlights.plague())
  }

  def getAllPlague = Action { implicit request =>
    val file = new File(Utils.dataDir, "plague.xlsx")
    val (columnNames, array) = Utils.getInfoByXlsxFile(file)
    val json = Json.obj("columnNames" -> columnNames, "array" -> array)
    Ok(json)
  }

  def expBefore = Action { implicit request =>
    val data = formTool.projectNameForm.bindFromRequest().get
    val files = tool.getFilesByProjectName(data.projectName)
    val b = files.nonEmpty
    val v1Projects = ArrayBuffer("PRJNA418486", "PRJNA407692-hibernate", "PRJNA407692-plague", "PRJNA114885",
      "PRJNA138223", "PRJNA395085", "PRJNA118017")
    if (v1Projects.contains(data.projectName)) {
      Ok(views.html.highlights.exp_v1(data.projectName))
    } else {
      Ok(views.html.highlights.exp(data.projectName, b))

    }
  }


  def expSearch = Action { implicit request =>
    val data = formTool.expForm.bindFromRequest().get
    val projectName = data.projectName
    val fileCoumnNames = data.comparisons
    val files = fileCoumnNames.map { vsName =>
      val dir = new File(Utils.expDir, projectName)
      new File(dir, s"${projectName}-${vsName}.txt")
    }
    val columnNames = ArrayBuffer("geneId") ++ fileCoumnNames
    val upColors = tool.getUpColors
    val downColors = tool.getDownColors
    val startTime = System.currentTimeMillis()
    val arrays = files.map { file =>
      val group = file.getName.replaceAll(s"${projectName}-", "").replaceAll(".txt", "")
      val lines = file.lines
      lines.drop(1).filterByColumns { columns =>
        Utils.isDouble(columns(1)) && Utils.isDouble(columns(2))
      }.mapByColumns { columns =>
        val value = BigDecimal(columns(1)).setScale(2, RoundingMode.HALF_UP).toString()
        columns(1) = value
        columns
      }.mapOtherByColumns { columns =>
        val value = columns(1)
        val pValue = if (columns(2) == "NA") "NA" else BigDecimal(columns(2)).setScale(8, RoundingMode.HALF_UP).toString()
        val geneId = columns(0)
        Json.obj("geneId" -> geneId, group -> Json.obj("pValue" -> pValue, "logFc" -> value))
      }
    }.reduceLeft((x, y) =>
      x.zip(y).map { case (i, j) =>
        i ++ j
      }
    )
    val allValues = arrays.flatMap { json =>
      json.\\("logFc").map(x => x.as[BigDecimal])
    }

    val min = allValues.min
    val max = allValues.max
    val upPiece = max / 100
    val downPiece = (0 - min) / 100
    val colorMap = allValues.map { value =>
      if (value >= 0) {
        val n = if (value == max) 99 else (value / upPiece).toInt
        (value, upColors(n))
      } else {
        val n = if (value == 0) 99 else ((value - min) / downPiece).toInt
        (value, downColors(n))
      }
    }.toMap
    val allArrays = arrays.map { json =>
      var curJson = json
      fileCoumnNames.foreach { columnName =>
        val tmpJson = json.\(columnName).as[JsObject]
        val logFc = tmpJson.\("logFc").as[BigDecimal]
        val color = colorMap(logFc)
        val columnJs = Json.obj("color" -> color) ++ tmpJson
        curJson ++= Json.obj(columnName -> columnJs)
      }
      curJson
    }.map { json =>
      var curJson = json
      fileCoumnNames.foreach { columnName =>
        val tmpJson = json.\(columnName).as[JsObject]
        val logFc = tmpJson.\("logFc").as[BigDecimal]
        val logFcAbs = logFc.abs
        val pValue = tmpJson.\("pValue").as[BigDecimal]
        val udShow = data.ud match {
          case "0" => true
          case "1" => logFc > 0
          case "-1" => logFc < 0
        }
        val fcRangeShow = data.min.map(x => logFcAbs >= x).getOrElse(true) && data.max.map(x => logFcAbs <= x).getOrElse(true)
        val pShow = data.pMin.map(x => pValue >= x).getOrElse(true) && data.pMax.map(x => pValue <= x).getOrElse(true)
        val show = udShow && fcRangeShow && pShow
        val columnJs = Json.obj("show" -> show) ++ tmpJson
        curJson ++= Json.obj(columnName -> columnJs)
      }
      curJson
    }
    val filterArrays = allArrays.filter { json =>
      val values = json.\\("show").map(x => x.as[Boolean])
      values.exists { value =>
        value
      }
    }
    val json = Json.obj("columnNames" -> columnNames, "array" -> filterArrays,
      "min" -> min, "max" -> max
    )
    Ok(json)
  }

  def getProjectInfo = Action { implicit request =>
    val data = formTool.projectNameForm.bindFromRequest().get
    val file = new File(Utils.dataDir, "附件5.2.txt")
    val array1 = Utils.getMapByFile(file)
    val file1 = new File(Utils.dataDir, "plague.txt")
    val array2 = Utils.getMapByFile(file1)
    val array = array1 ++ array2
    val vsNames = tool.getVsNames(data.projectName)
    val project = array.find { map =>
      map("Project ID") == data.projectName
    }.get
    Ok(Json.toJsObject(project) ++ Json.obj("vsNames" -> vsNames))
  }

  def getProjectInfoV1 = Action { implicit request =>
    val data = formTool.expV1Form.bindFromRequest().get
    val file = new File(Utils.dataDir, "附件5.2.txt")
    val array1 = Utils.getMapByFile(file)
    val file1 = new File(Utils.dataDir, "plague.txt")
    val array2 = Utils.getMapByFile(file1)
    val array = array1 ++ array2
    val vsNames = tool.getVsNamesV1(data.projectName, data.tissue)
    val project = array.find { map =>
      map("Project ID") == data.projectName
    }.get
    Ok(Json.toJsObject(project) ++ Json.obj("vsNames" -> vsNames))
  }

  def getTissues = Action { implicit request =>
    val data = formTool.projectNameForm.bindFromRequest().get
    val tissues = tool.getVsNames(data.projectName).map(_.split("\\s+")(0)).distinct
    val finalTissues = data.projectName match {
      case "PRJNA395085" => ArrayBuffer("lung", "mediastinal lymph nodes", "submandibular lymph nodes")
      case "PRJNA118017" => ArrayBuffer("lung", "spleen", "liver")
      case _ => tissues
    }
    Ok(Json.toJson(finalTissues))
  }


}
