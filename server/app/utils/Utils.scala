package utils

import java.io.{File, FileInputStream}
import java.lang.reflect.Field
import java.security.Security
import java.text.SimpleDateFormat
import java.util.Properties

import controllers.PageData
import javax.mail.{Message, Session}
import javax.mail.internet.{InternetAddress, MimeMessage, MimeUtility}
import org.apache.commons.io.{FileUtils, IOUtils}
import models.Tables._
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.{Cell, DateUtil}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc.Result
import tool.Pojo.{GeneGoJson, GeneGoSeq}

import scala.sys.process.ProcessLogger
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.math._
import utils.Implicits._


object Utils {

  val windowsPath = "D:\\marmot_database"
  val playPath = "/root/projects/play"
  val linuxPath = s"${playPath}/marmot_database"
  val path = {
    if (new File(windowsPath).exists()) windowsPath else linuxPath
  }
  var data: Seq[AllinfoRow] = Seq()

  val samtoolsPath = if (new File(windowsPath).exists()) new File(Utils.path, "samtools-0.1.19/samtools").getAbsolutePath else "samtools"
  val seqsDir = new File(Utils.path, "seqs")
  val blastFile = new File(Utils.path, "ncbi-blast-2.6.0+/bin/blastn")
  val blastBinFile = new File(Utils.path, "ncbi-blast-2.6.0+/bin")
  val dataDir = new File(Utils.path, "data")
  val enrichDir = new File(Utils.dataDir, "enrich")
  val expDir = new File(dataDir, "exp")
  val downloadDir = new File(Utils.dataDir, "download")
  val blast2HtmlFile = new File(Utils.path, "blast2html-82b8c9722996/blast2html.py")
  val blast2HtmlBinFile = new File(Utils.path, "blast2html-82b8c9722996")
  val svBin = new File("/mnt/sdb/linait/pipeline/MicroGenome_pipeline/MicroGenome_pipeline_v3.0/src/SV_finder_2.2.1/bin/")

  val muscleCommand = {
    if (new File(windowsPath).exists()) s"${windowsPath}\\muscle3.8.31_i86win32.exe" else "muscle"

  }

  def result2Future(result: Result) = {
    Future.successful(result)
  }

  def getMd5(file: File) = {
    val is = new FileInputStream(file)
    val md5 = DigestUtils.md5Hex(is).mkString
    is.close()
    md5
  }

  def getInfoByFile(file: File) = {
    val lines = FileUtils.readLines(file).asScala
    getInfoByLines(lines)
  }

  def getInfoByXlsxFile(xlsxFile: File) = {
    val lines = Utils.xlsx2Lines(xlsxFile)
    getInfoByLines(lines)
  }

  def getInfoByLines(lines: mutable.Buffer[String]) = {
    val columnNames = lines.head.split("\t")
    val array = lines.drop(1).map { line =>
      val columns = line.split("\t").map { x =>
        x.replaceAll("^\"", "").replaceAll("\"$", "")
      }
      columnNames.zip(columns).toMap
    }
    (columnNames, array)
  }

  def getMapByFile(file: File) = {
    val lines = file.lines
    val headers = lines.headers
    lines.drop(1).mapOtherByColumns { columns =>
      headers.zip(columns).toMap
    }
  }

  val isWindows = {
    if (new File(windowsPath).exists()) true else false
  }
  val windowsTestDir = new File("G:\\temp")
  val linuxTestDir = new File(playPath, "workspace")
  val testDir = if (windowsTestDir.exists()) windowsTestDir else linuxTestDir
  val goPy = {
    val path = "C:\\Python\\python.exe"
    if (new File(path).exists()) path else "python"
  }

  val scriptHtml =
    """
      |<script>
      |	$(function () {
      |			    $("footer:first").remove()
      |        $("#content").css("margin","0")
      |       $(".linkheader>a").each(function () {
      |				   var text=$(this).text()
      |				   $(this).replaceWith("<span style='color: #222222;'>"+text+"</span>")
      |			   })
      |
      |      $("tr").each(function () {
      |         var a=$(this).find("td>a:last")
      |					var text=a.text()
      |					a.replaceWith("<span style='color: #222222;'>"+text+"</span>")
      |				})
      |
      |       $("p.titleinfo>a").each(function () {
      |				   var text=$(this).text()
      |				   $(this).replaceWith("<span style='color: #606060;'>"+text+"</span>")
      |			   })
      |
      |       $(".param:eq(1)").parent().hide()
      |       $(".linkheader").hide()
      |
      |			})
      |</script>
    """.stripMargin

  val phylotreeCss =
    """
      |<style>
      |.tree-selection-brush .extent {
      |    fill-opacity: .05;
      |    stroke: #fff;
      |    shape-rendering: crispEdges;
      |}
      |
      |.tree-scale-bar text {
      |  font: sans-serif;
      |}
      |
      |.tree-scale-bar line,
      |.tree-scale-bar path {
      |  fill: none;
      |  stroke: #000;
      |  shape-rendering: crispEdges;
      |}
      |
      |.node circle, .node ellipse, .node rect {
      |fill: steelblue;
      |stroke: black;
      |stroke-width: 0.5px;
      |}
      |
      |.internal-node circle, .internal-node ellipse, .internal-node rect{
      |fill: #CCC;
      |stroke: black;
      |stroke-width: 0.5px;
      |}
      |
      |.node {
      |font: 10px sans-serif;
      |}
      |
      |.node-selected {
      |fill: #f00 !important;
      |}
      |
      |.node-collapsed circle, .node-collapsed ellipse, .node-collapsed rect{
      |fill: black !important;
      |}
      |
      |.node-tagged {
      |fill: #00f;
      |}
      |
      |.branch {
      |fill: none;
      |stroke: #999;
      |stroke-width: 2px;
      |}
      |
      |.clade {
      |fill: #1f77b4;
      |stroke: #444;
      |stroke-width: 2px;
      |opacity: 0.5;
      |}
      |
      |.branch-selected {
      |stroke: #f00 !important;
      |stroke-width: 3px;
      |}
      |
      |.branch-tagged {
      |stroke: #00f;
      |stroke-dasharray: 10,5;
      |stroke-width: 2px;
      |}
      |
      |.branch-tracer {
      |stroke: #bbb;
      |stroke-dasharray: 3,4;
      |stroke-width: 1px;
      |}
      |
      |
      |.branch-multiple {
      |stroke-dasharray: 5, 5, 1, 5;
      |stroke-width: 3px;
      |}
      |
      |.branch:hover {
      |stroke-width: 10px;
      |}
      |
      |.internal-node circle:hover, .internal-node ellipse:hover, .internal-node rect:hover {
      |fill: black;
      |stroke: #CCC;
      |}
      |
      |.tree-widget {
      |}
      |</style>
    """.stripMargin

  def getTime(startTime: Long) = {
    val endTime = System.currentTimeMillis()
    val time = (endTime - startTime) / 1000.0
    time
  }

  def printTime(f: () => Any) = {
    val startTime = System.currentTimeMillis()
    f()
    val endTime = System.currentTimeMillis()
    val time = (endTime - startTime) / 1000.0
    println(time)
  }

  val sender = Sender("VGsoft Team", "smtp.exmail.qq.com", "notice@vgbioteam.com", "Abc1144612652")


  val rPath = {
    val windowsPath = "D:\\workspaceForIDEA\\marDatabase\\rScripts"
    val linuxPath = s"${path}/rScripts"
    if (new File(windowsPath).exists()) windowsPath else linuxPath
  }

  val pyPath = {
    val windowsPath = "D:\\workspaceForIDEA\\marmot_scala_js\\server\\pyScripts"
    val linuxPath = s"${path}/pyScripts"
    if (new File(windowsPath).exists()) windowsPath else linuxPath
  }

  def callScript(tmpDir: File, shBuffer: ArrayBuffer[String]) = {
    val execCommand = new ExecCommand
    val runFile = if (Utils.isWindows) {
      new File(tmpDir, "run.bat")
    } else {
      new File(tmpDir, "run.sh")
    }
    FileUtils.writeLines(runFile, shBuffer.asJava)
    val shCommand = runFile.getAbsolutePath
    if (Utils.isWindows) {
      execCommand.exec(shCommand, tmpDir)
    } else {
      val useCommand = "chmod +x " + runFile.getAbsolutePath
      val dos2Unix = "dos2unix " + runFile.getAbsolutePath
      execCommand.exec(dos2Unix, useCommand, shCommand, tmpDir)
    }
    execCommand
  }

  def callLinuxScript(tmpDir: File, shBuffer: ArrayBuffer[String]) = {
    val execCommand = new ExecCommand
    val runFile = new File(tmpDir, "run.sh")
    FileUtils.writeLines(runFile, shBuffer.asJava)
    val dos2Unix = s"${Utils.command2Wsl("dos2unix")} ${runFile.unixPath} "
    val shCommand = s"${Utils.command2Wsl("sh")} ${runFile.unixPath}"
    execCommand.exec(dos2Unix, shCommand, tmpDir)
    execCommand
  }

  def command2Wsl(command: String) = {
    if (isWindows) s"wsl ${command}" else command
  }


  def splitByTab(str: String) = str.split("\t").toBuffer

  def isDouble(value: String): Boolean = {
    try {
      value.toDouble
    } catch {
      case _: Exception =>
        return false
    }
    true
  }

  def deleteDirectory(direcotry: File): Unit = {
    try {
      FileUtils.deleteDirectory(direcotry)
    } catch {
      case _ =>
    }
  }

  val pyScript =
    """
      |<script>
      |Plotly.Plots.resize(document.getElementById($('#charts').children().eq(0).attr("id")));
      |window.addEventListener("resize", function (ev) {
      |				Plotly.Plots.resize(document.getElementById($('#charts').children().eq(0).attr("id")));
      |					})
      |</script>
      |
    """.stripMargin

  def deleteDirectory(tmpDir: String): Unit = {
    val direcotry = new File(tmpDir)
    deleteDirectory(direcotry)
  }

  def dealInputFile(file: File) = {
    val lines = FileUtils.readLines(file).asScala
    val buffer = lines.map(_.trim)
    FileUtils.writeLines(file, buffer.asJava)
  }

  case class Sender(nick: String, host: String, email: String, password: String)

  case class Info(subject: String, content: String)

  def sendEmail(sender: Sender, info: Info, inbox: String) = {
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    val mailSession = Session.getDefaultInstance(props)
    val transport = mailSession.getTransport("smtp")
    val message = new MimeMessage(mailSession)
    val nick = MimeUtility.encodeText(sender.nick)
    message.setSubject(info.subject)
    message.setFrom(new InternetAddress(s"${nick}<${sender.email}>"))
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(inbox))
    message.setContent(info.content, "text/html;charset=utf-8")
    transport.connect(sender.host, sender.email, sender.password)
    transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO))
    transport.close()
  }

  def sendEmailBySsl(sender: Sender, info: Info, inbox: String) = {
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider())
    val SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"
    props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY)
    props.setProperty("mail.smtp.socketFactory.fallback", "false")
    props.setProperty("mail.smtp.port", "465")
    props.setProperty("mail.smtp.socketFactory.port", "465")
    val mailSession = Session.getDefaultInstance(props)
    val transport = mailSession.getTransport("smtp")
    val message = new MimeMessage(mailSession)
    val nick = MimeUtility.encodeText(sender.nick)
    message.setSubject(info.subject)
    message.setFrom(new InternetAddress(s"${nick}<${sender.email}>"))
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(inbox))
    message.setContent(info.content, "text/html;charset=utf-8")
    transport.connect(sender.host, sender.email, sender.password)
    transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO))
    transport.close()
  }

  def lines2File(file: File, lines: mutable.Buffer[String], append: Boolean = false) = {
    FileUtils.writeLines(file, lines.asJava, append)
  }

  def file2Lines(file: File) = {
    FileUtils.readLines(file).asScala
  }

  def xlsx2Lines(xlsxFile: File, sheetIndex: Int): ArrayBuffer[String] = {
    val is = new FileInputStream(xlsxFile.getAbsolutePath)
    val xssfWorkbook = new XSSFWorkbook(is)
    val xssfSheet = xssfWorkbook.getSheetAt(sheetIndex)
    val lines = ArrayBuffer[String]()
    for (i <- 0 to xssfSheet.getLastRowNum) {
      val columns = ArrayBuffer[String]()
      val xssfRow = xssfSheet.getRow(i)
      if (xssfRow != null) {
        for (j <- 0 until xssfRow.getLastCellNum) {
          val cell = xssfRow.getCell(j)
          var value = ""
          if (cell != null) {
            cell.getCellType match {
              case Cell.CELL_TYPE_STRING =>
                value = cell.getStringCellValue
              case Cell.CELL_TYPE_NUMERIC =>
                if (DateUtil.isCellDateFormatted(cell)) {
                  val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
                  value = dateFormat.format(cell.getDateCellValue)
                } else {
                  val doubleValue = cell.getNumericCellValue
                  value = if (doubleValue == doubleValue.toInt) {
                    doubleValue.toInt.toString
                  } else doubleValue.toString
                }
              case Cell.CELL_TYPE_BLANK =>
                value = ""
              case _ =>
                value = ""
            }
          }

          columns += value
        }
      }
      val line = columns.mkString("\t")
      lines += line
    }
    xssfWorkbook.close()
    lines.filter(StringUtils.isNotBlank(_))
  }

  def xlsx2Lines(xlsxFile: File): ArrayBuffer[String] = {
    xlsx2Lines(xlsxFile, 0)
  }

  def xlsx2Txt(xlsxFile: File, txtFile: File, sheetIndex: Int = 0) = {
    val lines = xlsx2Lines(xlsxFile, sheetIndex)
    FileUtils.writeLines(txtFile, lines.asJava)
  }

  def getArrayByTs[T](x: Seq[T]) = {
    x.map { y =>
      getMapByT(y)
    }
  }

  def getArrayByTs[T](x: Seq[T],jsonField:String) = {
    x.map { y =>
      getMapByT(y,jsonField)
    }
  }

  def getMapByT[T](t: T,jsonField:String) = {
    t.getClass.getDeclaredFields.toBuffer.map { x: Field =>
      x.setAccessible(true)
      val kind = x.get(t)
      val value = getValue(kind)
      (x.getName, value)
    }.init.toMap
  }

  def getMapByT[T](t: T) = {
    t.getClass.getDeclaredFields.toBuffer.map { x: Field =>
      x.setAccessible(true)
      val kind = x.get(t)
      val value = getValue(kind)
      (x.getName, value)
    }.init.toMap
  }

  def getValue[T](kind: T, noneMessage: String = "暂无"): String = {
    kind match {
      case x if x.isInstanceOf[DateTime] => val time = x.asInstanceOf[DateTime]
        time.toString("yyyy-MM-dd HH:mm:ss")
      case x if x.isInstanceOf[Option[T]] => val option = x.asInstanceOf[Option[T]]
        if (option.isDefined) getValue(option.get, noneMessage) else noneMessage
      case _ => kind.toString
    }
  }

  def getBase64Str(imageFile: File): String = {
    val inputStream = new FileInputStream(imageFile)
    val bytes = IOUtils.toByteArray(inputStream)
    val bytes64 = Base64.encodeBase64(bytes)
    inputStream.close()
    new String(bytes64)
  }

  def getValues[T](y: T) = {
    y.getClass.getDeclaredFields.toBuffer.map { x: Field =>
      x.setAccessible(true)
      x.get(y).toString
    }.init
  }

  def getValuesByFields[T](y: T, fields: Seq[String]) = {
    fields.map { fieldStr =>
     getValueByField(y,fieldStr)
    }

  }

  def getValueByField[T](y: T, fieldStr: String) = {
    val field: Field = y.getClass.getDeclaredField(fieldStr)
    field.setAccessible(true)
    field.get(y).toString
  }

  case class KeywordInfo(remainKeywords: mutable.Buffer[String], map: Map[String, String])


  def getSearchMap[T](keywordsMap: Map[T, KeywordInfo], idField: String, fields: Seq[String]) = {
    keywordsMap.map { case (y, keywordInfo) =>
      val texts = Utils.getValuesByFields(y, fields)
      val field: Field = y.getClass.getDeclaredField(idField)
      field.setAccessible(true)
      val id = field.get(y).toString
      val keywords = keywordInfo.remainKeywords
      val map = keywords.map { z =>
        val matchInfo = z.toUpperCase
        (z, texts.find(_.toUpperCase.indexOf(matchInfo) != -1).getOrElse(""))
      }.filter(x => StringUtils.isNotEmpty(x._2)).toMap
      val remainKeywords = keywords -- map.keySet
      (id, KeywordInfo(remainKeywords, keywordInfo.map ++ map))
    }

  }

  def getGeneGoSearchMap(keywordsMap: Map[GeneGoSeq, KeywordInfo]) = {
    keywordsMap.map { case (y, keywordInfo) =>
      val texts =y.seq
      val id = y.geneId
      val keywords = keywordInfo.remainKeywords
      val map = keywords.map { z =>
        val matchInfo = z.toUpperCase
        (z, texts.find(_.toUpperCase.indexOf(matchInfo) != -1).getOrElse(""))
      }.filter(x => StringUtils.isNotEmpty(x._2)).toMap
      val remainKeywords = keywords -- map.keySet
      (id, KeywordInfo(remainKeywords, keywordInfo.map ++ map))
    }

  }


  def dealDataByPage[T](x: Seq[T], page: PageData): Seq[T] = {
    val searchX = x.filter { y =>
      page.search match {
        case None => true
        case Some(text) =>
          val array = text.split("\\s+").map(_.toUpperCase).toBuffer
          val texts = getValues(y)
          array.forall { z =>
            texts.map(_.toUpperCase.indexOf(z) != -1).reduce(_ || _)
          }
      }
    }

    val sortX = page.sort match {
      case None => searchX
      case Some(y) =>
        val b = searchX.take(1000).forall { tmpData =>
          val method = tmpData.getClass.getDeclaredMethod(y)
          val value = method.invoke(tmpData).toString
          Utils.isDouble(value)
        }
        if (b) {
          searchX.sortBy { z =>
            val method = z.getClass.getDeclaredMethod(y)
            method.invoke(z).toString.toDouble
          }
        } else {
          searchX.sortBy { z =>
            val method = z.getClass.getDeclaredMethod(y)
            method.invoke(z).toString
          }
        }
    }

    val orderX = page.order match {
      case "asc" => sortX
      case "desc" => sortX.reverse
    }

    orderX

  }

  def dataDuplicate(dataFile: File) = {
    val dataLines = FileUtils.readLines(dataFile).asScala
    val newHeaders = dataLines.head.split("\t")
    val newLines = dataLines.drop(1).map { x =>
      val columns = x.split("\t").toBuffer
      (columns.head, columns.tail)
    }.groupBy(_._2).mapValues(x => x.map(_._1).mkString(",")).map { case (x, y) => (y, x) }
    val newBuffer = mutable.Buffer[mutable.Buffer[String]]()
    newBuffer += newHeaders.toBuffer
    newBuffer ++= newLines.map {
      case (x, y) => val line = mutable.Buffer[String]()
        line += x
        line ++= y
    }
    FileUtils.writeLines(dataFile, newBuffer.map(_.mkString("\t")).asJava)
  }

  def log2(x: Double): Double = log10(x) / log10(2.0)

  def execFuture[T](f: Future[T]): T = {
    Await.result(f, Duration.Inf)
  }


}
