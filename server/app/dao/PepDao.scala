package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PepDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[PepRow]): Future[Unit] = {
    db.run(Pep ++= rows).map(_ => ())
  }

  def deleteAll: Future[Unit] = db.run(Pep.delete).map(_ => ())

  def selectByGeneId(geneId: String): Future[Option[PepRow]] = db.run(Pep.filter(_.geneid === geneId).result.headOption)


}
