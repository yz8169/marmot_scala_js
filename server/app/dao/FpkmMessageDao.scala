package dao

import javax.inject.Inject

import controllers.RegionData
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FpkmMessageDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def deleteAll: Future[Unit] = db.run(Fpkmmessage.delete).map(_ => ())

  def insertAll(rows: Seq[FpkmmessageRow]): Future[Unit] = {
    db.run(Fpkmmessage ++= rows).map(_ => ())
  }

  def selectAll: Future[Seq[FpkmmessageRow]] = db.run(Fpkmmessage.result)

}
