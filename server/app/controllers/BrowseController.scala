package controllers

import dao._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import utils.Utils
import tool.FormTool
import play.api.libs.concurrent.Execution.Implicits._

import scala.collection.mutable.ArrayBuffer


/**
  * Created by yz on 2019/1/9
  */
class BrowseController @Inject()(cc: ControllerComponents, hibernationDiffGeneDao: HibernationDiffGeneDao,
                                 formTool: FormTool, goDao: GoDao, keggDao: KeggDao) extends AbstractController(cc) {

  def hibernateDiffGeneBefore = Action { implicit request =>
    Ok(views.html.browse.hibernateDiffGene())

  }

  def goBefore = Action { implicit request =>
    Ok(views.html.browse.go())
  }

  def getGo = Action.async { implicit request =>
    val data = formTool.goForm.bindFromRequest().get
    goDao.selectAll(data.kinds,data.species).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def getGoInfo = Action.async { implicit request =>
    goDao.selectAll.map { x =>
      val kinds = x.map(_.kind).distinct
      val species=x.map(_.species).distinct
      Ok(Json.obj("kinds"->kinds,"species"->species))
    }
  }


  def keggBefore = Action { implicit request =>
    Ok(views.html.browse.kegg())
  }

  def getAllKegg = Action.async { implicit request =>
    val data = formTool.keggForm.bindFromRequest().get
    keggDao.selectAll(data.species).map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def getKeggSpecies = Action.async { implicit request =>
    keggDao.selectAll.map { x =>
      val species = x.map(_.kind).distinct
      Ok(Json.toJson(species))
    }
  }

  def getAllHibernationDiffGene = Action.async { implicit request =>
    hibernationDiffGeneDao.selectAll.map { x =>
      val array = Utils.getArrayByTs(x.take(1000))
      Ok(Json.toJson(array))
    }
  }

  def getAllChr = Action.async { implicit request =>
    hibernationDiffGeneDao.selectAllChr.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllTissue = Action.async { implicit request =>
    hibernationDiffGeneDao.selectAllTissue.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllPlatform = Action.async { implicit request =>
    hibernationDiffGeneDao.selectAllPlatform.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllSpecies = Action.async { implicit request =>
    hibernationDiffGeneDao.selectAllSpecies.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def searchHDG = Action.async {
    implicit request =>
      val data = formTool.hDGSearchForm.bindFromRequest().get
      hibernationDiffGeneDao.selectByData(data).map { x =>
        val array = Utils.getArrayByTs(x)
        Ok(Json.toJson(array))
      }
  }

  def blockBefore = Action { implicit request =>
    Ok(views.html.browse.block())
  }

}
