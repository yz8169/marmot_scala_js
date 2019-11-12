package myJs

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.annotation._


@JSExportTopLevel("MySvg")
object MySvg {

  @JSExport("init")
  def init = {
    refreshSvg

  }

  def refreshSvg={
    import scalatags.Text.svgTags._
    import scalatags.Text.svgAttrs._
    val svgStr=svg(height:=800,width:=1000,
      defs(
        linearGradient(id:="gColor1",x1:="0%",y1:="0%",x2:="100%",y2:="100%",
          stop(offset:="0%",stopColor:="#02FF00",stopOpacity:=1),
          stop(offset:="100%",stopColor:="#D6FF00",stopOpacity:=1),
        )
      ),
      g(fill:="none",
        rect(x:=0,width:="300",height:=100,fill:="url(#gColor1)"),
        rect(x:=300,width:="300",height:=100,fill:="#FF5C00")
      )
    ).render
    $("#svg").html(svgStr)

  }



}
