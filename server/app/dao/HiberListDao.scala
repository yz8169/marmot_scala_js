package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.jdbc.JdbcProfile


/**
  * Created by yz on 2019/1/9
  */
class HiberListDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[HiberListRow]) = {
    db.run(HiberList ++= rows).map(_ => ())
  }

  def deleteAll = db.run(HiberList.delete).map(_ => ())

  def selectAll = db.run(HiberList.result)

  def selectByGeneId(geneId: String) = db.run(HiberList.filter(_.geneId === geneId).result)

  def selectByGeneIdO(geneId: String) = db.run(HiberList.filter(_.geneId === geneId).result.headOption)

}
