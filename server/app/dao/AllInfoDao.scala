package dao

import javax.inject.Inject

import controllers.RegionData
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AllInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def deleteAll: Future[Unit] = db.run(Allinfo.delete).map(_ => ())

  def insertAll(rows: Seq[AllinfoRow]): Future[Unit] = {
    db.run(Allinfo ++= rows).map(_ => ())
  }

  def selectAll: Future[Seq[AllinfoRow]] = db.run(Allinfo.result)

  def selectByGeneNames(geneNames: Seq[String]): Future[Seq[AllinfoRow]] = db.
    run(Allinfo.filter(_.genename.inSetBind(geneNames)).result)

  def selectAllGeneNames: Future[Seq[String]] = db.run(Allinfo.map(_.genename).result)

  def selectAllGeneIds: Future[Seq[String]] = db.run(Allinfo.map(_.geneid).result)

  def selectByGeneId(geneId: String): Future[(AllinfoRow, CdsRow, PepRow)] = db.run(Allinfo.join(Cds).on(_.geneid === _.geneid).join(Pep).
    on(_._1.geneid === _.geneid).map(x => (x._1._1, x._1._2, x._2)).filter(_._1.geneid === geneId).result.head)

  def selectByGeneIds(geneIds: Seq[String]): Future[Seq[AllinfoRow]] = db.
    run(Allinfo.filter(_.geneid.inSetBind(geneIds)).result)

  def selectByRegionData(data: RegionData): Future[Seq[AllinfoRow]] = db.run(Allinfo.filter(_.chr === data.chr)
    .filter(_.start >= data.start).filter(_.end <= data.end).result)

  def selectAllChrs: Future[Seq[String]] = db.run(Allinfo.map(_.chr).distinct.result)

}
