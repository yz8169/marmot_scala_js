package test

import tool.Tool

/**
  * Created by yz on 2019/4/20
  */
object Test1 {

  def main(args: Array[String]): Unit = {

    val term="a(GO:4568)a"
    println{
      Tool.getGoTerm(term)
    }

  }

}
