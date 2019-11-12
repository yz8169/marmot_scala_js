package dao

import controllers.RegionData
import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * Created by yz on 2019/1/9
 */
class BasicDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[BasicRow]) = {
    db.run(Basic ++= rows).map(_ => ())
  }

  def deleteAll = db.run(Basic.delete).map(_ => ())

  def selectAll = db.run(Basic.result)

  def selectAllChrs(species: String) = db.run(Basic.filter(_.species === species).map(_.chr).
    distinct.result)

  def selectAll(geneIds: Seq[String]) = db.run(Basic.filter(_.id.
    inSetBind(geneIds)).result)

  def selectByGeneIds(geneIds: Seq[String]) = db.
    run(Basic.filter(_.id.inSetBind(geneIds)).result)

  def selectByGeneIds(geneIds: Seq[String], species: String) = db.
    run(Basic.filter(_.species === species).filter(_.id.inSetBind(geneIds)).result)

  def selectByGeneId(geneId: String) = db.run(Basic.join(Cds).on(_.id === _.geneid).
    join(Pep).on(_._1.id === _.geneid).map(x => (x._1._1, x._1._2, x._2)).filter(_._1.id === geneId).result.head)

  def selectBasicByGeneId(geneId: String) = db.run(Basic.filter(_.id === geneId).
    result.head)

  def selectByGeneNames(geneNames: Seq[String]) = db.
    run(Basic.filter(_.symbol.inSetBind(geneNames)).result)

  def selectByGeneNames(geneNames: Seq[String], species: String) = db.
    run(Basic.filter(_.species === species).filter(_.symbol.inSetBind(geneNames)).result)

  def selectByRegionData(data: RegionData) = db.run(Basic.filter(_.chr === data.chr)
    .filter(_.start >= data.start).filter(_.end <= data.end).result)

  def selectByRegionData(data: RegionData, species: String) = db.run(Basic.
    filter(_.species === species).filter(_.chr === data.chr).filter(_.start >= data.start).filter(_.end <= data.end).
    result)

}
