package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.jdbc.JdbcProfile


/**
  * Created by yz on 2019/1/9
  */
class OrthologsDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[OrthologsRow]) = {
    db.run(Orthologs ++= rows).map(_ => ())
  }

  def deleteAll = db.run(Orthologs.delete).map(_ => ())

  def selectAll = db.run(Orthologs.result)

  def selectByGeneIdO(geneId: String) = db.run(Orthologs.
    filter(_.geneId === geneId).result.headOption)

}
