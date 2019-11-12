package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CdsDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[CdsRow]): Future[Unit] = {
    db.run(Cds ++= rows).map(_ => ())
  }

  def deleteAll: Future[Unit] = db.run(Cds.delete).map(_ => ())

//  def selectByGeneId(geneId: String): Future[Option[BgcdsRow]] = db.run(Bgcds.filter(_.id === geneId).result.headOption)


}
