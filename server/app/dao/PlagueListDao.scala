package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.jdbc.JdbcProfile


/**
  * Created by yz on 2019/1/9
  */
class PlagueListDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[PlagueListRow]) = {
    db.run(PlagueList ++= rows).map(_ => ())
  }

  def deleteAll = db.run(PlagueList.delete).map(_ => ())

  def selectAll = db.run(PlagueList.result)

  def selectByGeneId(geneId: String) = db.run(OtherOrthologs.filter(_.himalayan === geneId).result)

  def selectByGeneIdO(geneId: String) = db.run(PlagueList.filter(_.geneId === geneId).result.headOption)

}
