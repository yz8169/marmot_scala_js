package implicits

import java.io.{File, FileInputStream}
import java.text.SimpleDateFormat

import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.{Cell, DateUtil}
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.collection.mutable.ArrayBuffer

/**
 * Created by Administrator on 2019/11/11
 */
class XlxsFile(file: File) {

  def lines(sheetIndex:Int) = {
    val is = new FileInputStream(file.getAbsolutePath)
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

}
