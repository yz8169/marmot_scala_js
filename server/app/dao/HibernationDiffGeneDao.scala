package dao

import javax.inject.Inject
import models.Tables.Pep
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import models.Tables._
import play.api.libs.concurrent.Execution.Implicits._
import tool.HDGSearchData


/**
  * Created by yz on 2019/1/9
  */
class HibernationDiffGeneDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[HibernationDiffGeneRow]): Future[Unit] = {
    db.run(HibernationDiffGene ++= rows).map(_ => ())
  }

  def deleteAll: Future[Unit] = db.run(HibernationDiffGene.delete).map(_ => ())

  def selectAll: Future[Seq[HibernationDiffGeneRow]] = db.run(HibernationDiffGene.result)

  def selectAllChr: Future[Seq[String]] = db.run(HibernationDiffGene.filter(_.chr.isDefined).
    map(_.chr).distinct.map(_.get).result)

  def selectAllTissue: Future[Seq[String]] = db.run(HibernationDiffGene.
    map(_.tissue).distinct.result)

  def selectAllPlatform: Future[Seq[String]] = db.run(HibernationDiffGene.
    map(_.platform).distinct.result)

  def selectAllSpecies: Future[Seq[String]] = db.run(HibernationDiffGene.
    map(_.species).distinct.result)

  def selectByData(data: HDGSearchData): Future[Seq[HibernationDiffGeneRow]] = db.run(HibernationDiffGene.filter { x =>
    data.chr match {
      case None => LiteralColumn(true)
      case Some(chr) => x.chr.map(_ === chr).getOrElse(false)
    }
  }.filter { x =>
    data.start match {
      case None =>LiteralColumn(true)
      case Some(start) => x.start.map(_ >= start).getOrElse(false)
    }
  }.filter { x =>
    data.end match {
      case None =>LiteralColumn(true)
      case Some(end) => x.start.map(_ <= end).getOrElse(false)
    }
  }.filter { x =>
    data.min match {
      case None =>LiteralColumn(true)
      case Some(min) => x.log2fc>=min
    }
  }.filter { x =>
    data.max match {
      case None =>LiteralColumn(true)
      case Some(max) => x.log2fc<=max
    }
  }.filter { x =>
    data.tissue match {
      case None =>LiteralColumn(true)
      case Some(tissue) => x.tissue===tissue
    }
  }.filter { x =>
    data.platform match {
      case None =>LiteralColumn(true)
      case Some(platform) => x.platform===platform
    }
  }.filter { x =>
    data.species match {
      case None =>LiteralColumn(true)
      case Some(species) => x.species===species
    }
  }.result)

}
