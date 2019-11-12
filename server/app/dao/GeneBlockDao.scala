package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.jdbc.JdbcProfile

import scala.concurrent.Future


/**
 * Created by yz on 2019/1/9
 */
class GeneBlockDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[GeneBlockRow]) = {
    db.run(GeneBlock ++= rows).map(_ => ())
  }

  def deleteAll = db.run(GeneBlock.delete).map(_ => ())

  def selectAll = db.run(GeneBlock.result)

  def selectAll(geneIds: Seq[String]) = db.run(GeneBlock.
    filter(_.aGeneId.inSetBind(geneIds)).result)

  def selectAllByBlock(blockId: String) = db.run(GeneBlock.
    filter(_.blockName === blockId).result)

  def selectByGeneId(geneId: String) = {
    db.run(GeneBlock.filter(_.aGeneId === geneId).result)
  }

  def selectAllKind = db.run(GeneBlock.map(_.kind).distinct.result)

  def selectByKinds(kinds: Seq[String]) = db.run(GeneBlock.
    filter(_.kind.inSetBind(kinds)).result)


}
