package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

class AppController @Inject()(cc:ControllerComponents) extends AbstractController(cc){

  def toIndex = Action {implicit request=>
    Ok(views.html.index())
  }

  def toHelp=Action{implicit request=>
    Ok(views.html.help())
  }

  def toHibernation = Action {implicit request=>
    Ok(views.html.hibernation())
  }

  def toIntroduction = Action {implicit request=>
    Ok(views.html.introduction())
  }

  def toSubmit = Action {implicit request=>
    Ok(views.html.submission())
  }

  def toAbout = Action {implicit request=>
    Ok(views.html.about())
  }

  def toFaq = Action {implicit request=>
    Ok(views.html.faq())
  }

  def toHow = Action {implicit request=>
    Ok(views.html.how())
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        controllers.routes.javascript.BrowseController.getGo,
        controllers.routes.javascript.SearchController.getDetailInfo,
        controllers.routes.javascript.BrowseController.getAllKegg,
        controllers.routes.javascript.BrowseController.getAllSpecies,
        controllers.routes.javascript.BrowseController.getAllPlatform,
        controllers.routes.javascript.BrowseController.getAllTissue,
        controllers.routes.javascript.BrowseController.getKeggSpecies,
        controllers.routes.javascript.BrowseController.getGoInfo,

        controllers.routes.javascript.SearchController.boxPlotByGeneId,
        controllers.routes.javascript.SearchController.barPlot,
        controllers.routes.javascript.SearchController.getAllGeneGo,
        controllers.routes.javascript.SearchController.getAllGeneKegg,
        controllers.routes.javascript.SearchController.getAllOrthologs,
        controllers.routes.javascript.SearchController.searchByKeyword,
        controllers.routes.javascript.SearchController.searchByGeneId,
        controllers.routes.javascript.SearchController.searchByGeneName,
        controllers.routes.javascript.SearchController.searchByRegion,
        controllers.routes.javascript.SearchController.getDegInfo,
        controllers.routes.javascript.SearchController.getAllChrs,
        controllers.routes.javascript.SearchController.getBasicInfo,

        controllers.routes.javascript.HighlightsController.getAllHibernators,
        controllers.routes.javascript.HighlightsController.getAllPlague,
        controllers.routes.javascript.HighlightsController.expBefore,
        controllers.routes.javascript.HighlightsController.getProjectInfo,
        controllers.routes.javascript.HighlightsController.getProjectInfoV1,
        controllers.routes.javascript.HighlightsController.getTissues,
        controllers.routes.javascript.HighlightsController.expSearch,
        controllers.routes.javascript.DownloadController.getGenomeData,
        controllers.routes.javascript.DownloadController.downloadData,

        controllers.routes.javascript.GeneBlockController.getGeneBlock,
        controllers.routes.javascript.GeneBlockController.blockInfoBefore,
        controllers.routes.javascript.GeneBlockController.blockInfo,
        controllers.routes.javascript.GeneBlockController.getKinds,
        controllers.routes.javascript.GeneBlockController.getBlocks,

        controllers.routes.javascript.AppController.toIntroduction,

        controllers.routes.javascript.ToolController.goEnrich,
        controllers.routes.javascript.ToolController.keggEnrich,





      )
    ).as("text/javascript")
  }



}
