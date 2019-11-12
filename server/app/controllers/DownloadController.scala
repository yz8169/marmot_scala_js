package controllers

import java.io.{File, FileInputStream}
import java.nio.file.Files
import java.util.zip.Checksum

import javax.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import play.api.mvc._
import tool.FormTool
import utils.Utils
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json

import scala.collection.mutable.ArrayBuffer


/**
  * Created by yz on 2018/7/25
  */
class DownloadController @Inject()(cc: ControllerComponents, formTool: FormTool) extends AbstractController(cc) {

  def toIndex = Action { implicit request =>
    Ok(views.html.download.index())
  }

  def download = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.path, data.fileName)
    Ok.sendFile(file).as("application/x-download").
      withHeaders(
        CACHE_CONTROL -> "max-age=3600",
        CONTENT_DISPOSITION -> ("attachment; filename=" + file.getName)
      )
  }

  def downloadData = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.downloadDir, data.fileName)
    Ok.sendFile(file).as("application/x-download").
      withHeaders(
        CACHE_CONTROL -> "max-age=3600",
        CONTENT_DISPOSITION -> ("attachment; filename=" + file.getName)
      )
  }

  def getGenomeData = Action { implicit request =>
    val file=new File(Utils.dataDir,"genome_md5.txt")
    val (columnNames, array) = Utils.getInfoByFile(file)
    val json = Json.obj("columnNames" -> columnNames, "array" -> array)
    Ok(json)
  }


}
