package controllers

import dao._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import tool.FormTool
import utils.Utils
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2019/4/12
  */
class GeneBlockController @Inject()(cc: ControllerComponents, formTool: FormTool, geneBlockDao: GeneBlockDao) extends AbstractController(cc) {

  def getGeneBlock = Action.async { implicit request =>
    val data = formTool.geneIdForm.bindFromRequest().get
    geneBlockDao.selectByGeneId(data.geneId).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def getKinds = Action.async { implicit request =>
    geneBlockDao.selectAllKind.map { x =>
      val array = x
      Ok(Json.toJson(array))
    }
  }

  def getBlocks = Action.async { implicit request =>
    val data = formTool.kindsForm.bindFromRequest().get
    geneBlockDao.selectByKinds(data.kinds).map { x =>
      val blocks = x
      case class BlockData(blockId: String, locationA: String, locationB: String)
      val blockDatas = blocks.groupBy(_.blockName).map { case (blockId, datas) =>
        val aChr = datas.head.aChr
        val aStart = datas.map(x => x.aStart).min
        val aEnd = datas.map(x => x.aEnd).max
        val aLocus = s"${aChr}:${aStart}-${aEnd}"
        val bChr = datas.head.bChr
        val bStart = datas.map(x => x.bStart).min
        val bEnd = datas.map(x => x.bEnd).max
        val bLocus = s"${bChr}:${bStart}-${bEnd}"
        BlockData(blockId, aLocus, bLocus)
      }.toList.sortBy(_.blockId.substring(3).toInt)
      val array = Utils.getArrayByTs(blockDatas)
      Ok(Json.toJson(array))
    }
  }

  def blockInfoBefore = Action { implicit request =>
    val data = formTool.blockIdForm.bindFromRequest().get
    Ok(views.html.search.blockInfo(data.blockId))
  }

  def blockInfo = Action.async { implicit request =>
    val data = formTool.blockIdForm.bindFromRequest().get
    geneBlockDao.selectAllByBlock(data.blockId).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }


}
