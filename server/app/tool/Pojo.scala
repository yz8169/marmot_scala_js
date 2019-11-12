package tool

import play.api.libs.json.JsValue

/**
 * Created by Administrator on 2019/11/5
 */
object Pojo {

  case class SpeciesData(species:String)

  case class GeneGoData(geneId:String,category:String,term:String,description:String)

  case class GeneGoJson(geneId:String,json:JsValue)

  case class GeneGoSeq(geneId:String,seq:Seq[String])

}
