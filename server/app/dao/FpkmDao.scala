package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FpkmDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def deleteAll: Future[Unit] = db.run(Fpkm.delete).map(_ => ())

  def insertAll(rows: Seq[FpkmRow]): Future[Unit] = {
    db.run(Fpkm ++= rows).map(_ => ())
  }

  def selectByProjectInfo(id: String, groupName: String): Future[Seq[FpkmRow]] = db.run(Fpkm.filter(_.geneid === id).
    filter(_.group === groupName).result)

  def selectByGeneId(id: String) = db.run(Fpkm.filter(_.geneid === id).result)

  def selectAllSampleNames: Future[Seq[String]] = db.run(Fpkm.map(_.samplename).distinct.result)

  def selectAll: Future[Seq[FpkmRow]] = db.run(Fpkm.result)


}
