package controllers

import javax.inject.Inject
import play.api.mvc._
import tool.FormTool
import utils.Utils
import utils.Utils.{Info, Sender}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2018/8/9
  */
class SubmitController @Inject()(formTool: FormTool) extends Controller {

  def toIndex = Action {implicit request=>
    Ok(views.html.submit.index())
  }

  def sendMail = Action { implicit request =>
    val data = formTool.submitForm.bindFromRequest().get
    val content =
      s"""
         |Name: ${data.name}<br>
         |Email: ${data.email}<br>
         |Subject: ${data.subject}<br>
         |Message: ${data.message}
       """.stripMargin
    val info = Info("旱獭分析平台用户数据上传通知", content)
    val inbox = "1454190131@qq.com"
    val sender = Sender("vg生信团队", "smtp.exmail.qq.com", "yinzheng@vgbioteam.com", "Abc1144612652")
    Utils.sendEmailBySsl(sender, info, inbox)
    Ok("success")
  }

}
