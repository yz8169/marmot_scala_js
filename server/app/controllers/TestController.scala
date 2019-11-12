package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

/**
  * Created by yz on 2019/3/18
  */
class TestController @Inject()(cc:ControllerComponents) extends AbstractController(cc) {

  def toTest = Action {implicit request=>
    Ok(views.html.test())
  }

}
