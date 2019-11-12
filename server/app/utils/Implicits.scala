package utils

import java.io.File

import implicits.XlxsFile
import org.apache.commons.io.FileUtils

import scala.collection.mutable
import scala.collection.JavaConverters._

/**
 * Created by yz on 2019/4/25
 */
object Implicits {

  implicit class MyFile(file: File) {

    def unixPath = {
      val path = file.getAbsolutePath
      path.toUnixPath
    }

    def lines = Utils.file2Lines(file)

    def toXlsxFile = {
      new XlxsFile(file)
    }

    def xlsxLines(sheetIndex: Int) = {
      new XlxsFile(file).lines(sheetIndex)
    }


  }

  implicit class MyString(v: String) {

    def isDouble: Boolean = {
      try {
        v.toDouble
      } catch {
        case _: Exception =>
          return false
      }
      true
    }

    def trimQuote = v.replaceAll("^\"", "").replaceAll("\"$", "")

    def toUnixPath = {
      v.replace("\\", "/").replaceAll("D:", "/mnt/d").
        replaceAll("E:", "/mnt/e").replaceAll("C:", "/mnt/c").
        replaceAll("G:", "/mnt/g")

    }

  }

  implicit class MyDouble(v: Double) {

    def toFixed(n: Int) = {
      v.formatted(s"%.${n}f")
    }

  }

  implicit class MyLines(val lines: mutable.Buffer[String]) {

    def filterByColumns(f: mutable.Buffer[String] => Boolean) = {
      lines.filter { line =>
        val columns = Utils.splitByTab(line)
        f(columns)
      }
    }

    def toFile(file: File, append: Boolean = false) = {
      FileUtils.writeLines(file, lines.asJava, append)
    }

    def mapByColumns(n: Int, f: mutable.Buffer[String] => mutable.Buffer[String]): mutable.Buffer[String] = {
      lines.take(n) ++= lines.drop(n).map { line =>
        val columns = Utils.splitByTab(line)
        val newColumns = f(columns)
        newColumns.mkString("\t")
      }

    }

    def mapByColumns(f: mutable.Buffer[String] => mutable.Buffer[String]): mutable.Buffer[String] = {
      mapByColumns(0, f)
    }

    def mapOtherByColumns[T](f: mutable.Buffer[String] => T) = {
      lines.map { line =>
        val columns = Utils.splitByTab(line)
        f(columns)
      }
    }

    def flatMapOtherByColumns[T](f: mutable.Buffer[String] => Seq[T]) = {
      lines.flatMap { line =>
        val columns = Utils.splitByTab(line)
        f(columns)
      }
    }

    def headers = lines.head.split("\t").toBuffer

  }

}
