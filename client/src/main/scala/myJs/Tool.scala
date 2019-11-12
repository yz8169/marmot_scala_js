package myJs

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js

/**
 * Created by Administrator on 2019/11/5
 */
object Tool {

  val species = ArrayBuffer("Himalayan marmot", "Alpine marmot", "Yellow-bellied marmot")

  def revDict(dict: js.Dictionary[String]) = {
    dict.groupBy(_._2).mapValues(x => x.map(_._1))
  }

}
