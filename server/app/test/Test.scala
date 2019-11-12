package test

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}

import org.apache.commons.lang3.StringUtils
import utils.Utils

import scala.io.Source
//import com.outr.lucene4s.field.{Field, FieldType}

import utils.Implicits._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
//import org.scalatest.{Matchers, WordSpec}


/**
  * Created by yz on 2019/2/21
  */
object Test {

  //  val parent = new File("E:\\marmot\\test")
  val projectName = "PRJNA407692-plague"
  val parent = new File("E:\\marmot\\test")

  def main(args: Array[String]): Unit = {
    //    productYellowKeggFile

      productUniqHeaderFile
      productDataFile
      productGroupFile
      distFile


    val groupMap = Map(
      "PRJNA395085" -> ArrayBuffer("lung", "submandibular lymph nodes","mediastinal lymph nodes"),
      "PRJNA118017" -> ArrayBuffer("lung", "spleen","liver"),
      "PRJNA407692-plague" -> ArrayBuffer("Liver", "Lung")
    )

    //    val runR=false
    val runR = true

    if (runR) {
      val preGroupFile = new File(parent, s"${projectName}_preGroup.txt")
      val groupFile = new File(parent, s"${projectName}_group.txt")
      if (groupMap.isDefinedAt(projectName)) {
        val lines = preGroupFile.lines
        groupMap(projectName).foreach { groupName =>
          lines.filter { line =>
            line.split("\t")(1).contains(groupName)
          }.toFile(groupFile)
          val rFile = new File("D:\\workspaceForIDEA\\marmot_scala_js\\server\\rScripts\\differenceAna.R")
          val command = s"Rscript ${rFile} --p ${projectName}"
          Utils.callScript(parent, ArrayBuffer(command))
        }


      } else {
        preGroupFile.lines.toFile(groupFile)
        val rFile = new File("D:\\workspaceForIDEA\\marmot_scala_js\\server\\rScripts\\differenceAna.R")
        val command = s"Rscript ${rFile} --p ${projectName}"
        Utils.callScript(parent, ArrayBuffer(command))
      }
    }




    //    val rFile = new File("D:\\workspaceForIDEA\\marmot_scalajs\\server\\rScripts\\colors.R")
    //    val lines=Utils.file2Lines(rFile)
    //    val command = s"Rscript ${rFile}"
    //    Utils.callScript(parent, ArrayBuffer(command))


    //    val file = new File(parent, "deal.txt")
    //    val lines = Utils.file2Lines(file)
    //    val ids = lines.drop(1).map(_.split("\t")(0))
    //    println(ids.head)
    //    println(ids.size)
    //    println(ids.distinct.size)


    //    val file=new File(parent, "gene_go.txt")
    //    val lines=Utils.file2Lines(file)
    //    val gos=lines.map(_.split("\t")(2))
    //    println(gos.take(3))
    //    println(gos.size)
    //    println(gos.distinct.size)


  }

  def rename={
    val dir=new File("D:\\databases\\hanta_database\\data\\exp\\PRJNA118017")
    dir.listFiles().foreach{file=>
      if(file.getName.contains("锟斤拷")){
        val distFile=new File(dir,file.getName.replaceAll("锟斤拷","Δ"))
        file.renameTo(distFile)
      }
    }

  }

  def annoCode = {

    //        val rows2 = Utils.xlsx2Lines(new File(parent, "Alpine-marmot-GO-KEGG.xlsx"), 1).drop(1).
    //          filter { line =>
    //            val columns = line.split("\t")
    //            columns.size > 3
    //          }
    //        println(rows2.size)

    //    val file = new File(parent, "Yellow-bellied_marmot_genomic.gff")
    //    val newLines = file.lines.filterByColumns { columns =>
    //      if (columns.size < 3) true else if (columns(2) == "region") false  else true
    //    }
    //    Utils.lines2File(new File(parent,"yellow.gff"),newLines)

    //    val dir = new File("E:\\hanta_database\\data")
    //    val files = dir.listFiles().filter(x => x.getName.contains("-"))
    //    val names = files.map(_.getName.split("-")(0)).distinct.toBuffer
    //    names.foreach { name =>
    //      val eachDir = new File(dir, s"exp/${name}")
    //      if (!eachDir.exists()) FileUtils.forceMkdir(eachDir)
    //     dir.listFiles().filter(x => x.getName.startsWith(name)).foreach { file =>
    //        FileUtils.copyFileToDirectory(file, eachDir)
    //      }
    //    }

    //    val bs = Source.fromFile(new File(parent, "Marmot_correct.raw.filter.vcf"))
    //    var index = 0
    //    val names = ArrayBuffer("Mongolia marmot", "Yellow-bellied marmot", "Long-tailed marmot", "Gray marmot",
    //      "albinistic Himalayan marmot")
    //    val outs = names.map { name =>
    //      new PrintWriter(new File(parent, s"${name}.vcf"))
    //    }
    //    var num=0
    //    bs.getLines().foreach { line =>
    //      num+=1
    //      if(num%50000==0) println(num)
    //      if (line.startsWith("##")) {
    //        outs.foreach(out => out.write(line + "\n"))
    //      } else if (line.startsWith("#")) {
    //        val columns = line.split("\t").toBuffer
    //        index = columns.indexOf("R21")
    //        outs.zipWithIndex.foreach { case (out, i) =>
    //          val newLine = (columns.take(index) += names(i)).mkString("\t")
    //          out.write(s"${newLine}\n")
    //        }
    //      } else {
    //        val columns = line.split("\t").toBuffer
    //        outs.zipWithIndex.foreach { case (out, i) =>
    //          val value = columns(index + i)
    //          val b = value.startsWith("0/0") | value.startsWith("./.")
    //          if (!b) {
    //            val newLine = (columns.take(index) += value).mkString("\t")
    //            out.write(s"${newLine}\n")
    //          }
    //
    //        }
    //      }
    //
    //    }
    //    bs.close()
    //    outs.foreach(out => out.close())

  }

  def productYellowKeggFile = {
    val file = new File(parent, "2marmots-annotation&ortholog.xlsx")
    val newLines = Utils.xlsx2Lines(file, 0)
    newLines.drop(1).mapOtherByColumns { columns =>
      s"${columns(0)}\t${columns(1)}"
    }.toFile(new File(parent, "yellow_kegg_anno.txt"))
  }

  def productHtml = {
    val file = new File("D:\\workspaceForIDEA\\marmot_scala_js\\server\\app\\test\\test.txt")
    println("<div>")
    val lines = file.lines.map { line =>
      val columns = line.split(":|：")
      val title = columns(0)
      val content = columns.drop(1).mkString
      title match {
        case "Common name" => s"""|<div class="myDiv" style="margin-top: 5px">
                                  |					<span class="mySpan">Common name</span>:
                                  |        ${content}
                                  |				</div>
       """.stripMargin
        case "Picture sources" => s"""|<div class="myDiv">
                                      |					<span class="mySpan">Picture sources</span>:
                                      |					<a target="_blank" href=" ${content}">
                                      |						 ${content}</a>
                                      |				</div>
       """.stripMargin
        case "Latin name" => s"""|<div class="myDiv">
                                 |					<span class="mySpan">Latin name</span>:
                                 |				<i>${content}</i>
                                 |				</div>
       """.stripMargin
        case _ => s"""|<div class="myDiv">
                      |        <span class="mySpan">${title}</span>:
                      |         ${content}
                      |      </div>
       """.stripMargin
      }

    }.foreach(print(_))
    println("</div>")
  }

  def productGroupFile = {
    val file = new File(parent, s"${projectName}_uniq_header_data.txt")
    val lines = Utils.file2Lines(file)
    val samples = lines(1).split("\t").toBuffer
    val headers = lines.headers
    val notEmptyHeaders = headers.zipWithIndex.filter { case (v, i) =>
      StringUtils.isNotEmpty(v)
    }
    val newLines = notEmptyHeaders.zipWithIndex.map { case (v, i) =>
      if (i < notEmptyHeaders.length - 1) {
        (v._2, notEmptyHeaders(i + 1)._2)
      } else (v._2, samples.length)
    }.flatMap { case (start, end) =>
      samples.slice(start, end).map(x => s"${x}\t${headers(start)}")
    }


    Utils.lines2File(new File(parent, s"${projectName}_preGroup.txt"), newLines)

  }

  def productDataFile = {
    val file = new File(parent, s"${projectName}_uniq_header_data.txt")
    val lines = Utils.file2Lines(file).filter(x => StringUtils.isNotBlank(x))
    val newLines = lines.drop(1).map { line =>
      val columns = line.split("\t").toBuffer
      //      (Array(columns(2)) ++ columns.drop(3)).mkString("\t")
//            (Array(columns(0)) ++ columns.drop(1)).mkString("\t")
            (Array(columns(1)) ++ columns.drop(2)).mkString("\t")
//      (Array(columns(2)) ++ columns.drop(3)).mkString("\t")
//            (Array(columns(1)) ++ columns.drop(3)).mkString("\t")
    }
    Utils.lines2File(new File(parent, s"${projectName}_data.txt"), newLines)
  }

  def productUniqHeaderFile = {
    val file = new File(parent, s"${projectName}.txt")
    val lines = Utils.file2Lines(file).filter(x => StringUtils.isNotBlank(x))
    val newHeaders = lines(1).split("\t").zipWithIndex.map(x => s"${
      x._1.replaceAll("^\"", "")
        .replaceAll("\"$", "")
    }_${x._2}").mkString("\t")
    val firstLine = lines.head.split("\t").map(x => x.replaceAll("^\"", "").
      replaceAll("\"$", "")).mkString("\t")
    lines(1) = newHeaders
    lines(0) = firstLine
    Utils.lines2File(new File(parent, s"${projectName}_uniq_header_data.txt"), lines)
  }

  def productGeneSymbolFile = {
    val file = new File(parent, s"${projectName}.txt")
    val lines = Utils.file2Lines(file).filter(StringUtils.isNotBlank(_))
    val newLines = lines.drop(1).map { line =>
      val columns = line.split("\t").toBuffer
      (ArrayBuffer(columns(0), columns(2))).mkString("\t")
    }
    Utils.lines2File(new File(parent, s"${projectName}_symbol.txt"), newLines)
  }

  def productEstFile = {
    val file = new File(parent, s"${projectName}.txt")
    val lines = Utils.file2Lines(file).filter(StringUtils.isNotBlank(_))
    val newLines = lines.drop(1).map { line =>
      val columns = line.split("\t").toBuffer
      (ArrayBuffer(columns(0), columns(1))).mkString("\t")
    }
    Utils.lines2File(new File(parent, s"${projectName}_est.txt"), newLines)
  }

  def distFile = {
    val file = new File(parent, s"${projectName}_data.txt")
    val lines = file.lines
    val map = mutable.LinkedHashMap[String, String]()
    lines.drop(1).filterByColumns{columns=>
      import utils.Implicits._
      columns.drop(1).forall(x=>x.isDouble)
    }.foreach { line =>
      val columns = line.split("\t")
      val key = columns(0)
      if (map.isDefinedAt(key)) {
        val currentSum = columns.drop(1).map(_.toDouble).sum
        val beforeSum = map(key).split("\t").drop(3).map(_.toDouble).sum
        if (currentSum >= beforeSum) map += (key -> line)
      } else {
        map += (key -> line)
      }
    }
    val headers = lines(1).split("\t")
    val newLines = lines.take(1) ++ map.values.toList.map { line =>
      line.split("\t").toBuffer.padTo(headers.size, "NA").mkString("\t")
    }.filter { line =>
      val columns = line.split("\t")
      columns.drop(1).forall(x => (Utils.isDouble(x) || x == "NA"))

    }

    newLines(0) = newLines(0).replaceAll("^#", "")

    Utils.lines2File(new File(parent, s"${projectName}_deal.txt"), newLines)

  }

  def dealKeggFile = {
    val file = new File(parent, "附件2.xlsx")
    val lines = Utils.xlsx2Lines(file, 1)
    val outFile = new File(parent, "kegg.txt")
    Utils.lines2File(outFile, lines)
  }

  def dealGoFile = {
    val file = new File(parent, "附件2.xlsx")
    val lines = Utils.xlsx2Lines(file)
    var kind = lines(0)
    val newLines = lines.map { line =>
      val columns = line.split("\t")
      if (columns.size == 1) {
        kind = line
        line
      } else {
        s"${kind}\t${line}"
      }
    }.filter(_.split("\t").size > 1)
    val outFile = new File(parent, "go.txt")
    Utils.lines2File(outFile, newLines)

  }


}
