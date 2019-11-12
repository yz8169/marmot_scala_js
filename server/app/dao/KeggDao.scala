package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.jdbc.JdbcProfile

import scala.concurrent.Future


/**
  * Created by yz on 2019/1/9
  */
class KeggDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[KeggRow]) = {
    db.run(Kegg ++= rows).map(_ => ())
  }

  def deleteAll = db.run(Kegg.delete).map(_ => ())

  def selectAll(species:Seq[String]) = db.run(Kegg.filter(_.kind.inSetBind(species)).result)

  def selectAll = db.run(Kegg.result)

}
