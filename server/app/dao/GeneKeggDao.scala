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
class GeneKeggDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[GeneKeggRow]) = {
    db.run(GeneKegg ++= rows).map(_ => ())
  }

  def deleteAll = db.run(GeneKegg.delete).map(_ => ())

  def selectAll = db.run(GeneKegg.result)

  def selectAll(ids:Seq[String]) = db.run(GeneKegg.
    filter(_.geneId.inSetBind(ids)).result)

  def selectByGeneId(geneId: String) = db.run(GeneKegg.filter(_.geneId === geneId).result)

}
