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
class GeneGoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[GeneGoRow]) = {
    db.run(GeneGo ++= rows).map(_ => ())
  }

  def deleteAll = db.run(GeneGo.delete).map(_ => ())

  def selectAll = db.run(GeneGo.result)

  def selectAll(geneIds: Seq[String]) = db.run(GeneGo.
    filter(_.geneId.inSetBind(geneIds)).result)

  def selectByGeneId(geneId: String): Future[Seq[GeneGoRow]] = db.run(GeneGo.filter(_.geneId === geneId).result)

  def fullSearch(value: String) = {
    val q = sql"""
            SELECT * FROM gene_go where MATCH(gene_id,category,term,description) AGAINST($value IN BOOLEAN MODE)
          """.as[GeneGoRow]
    db.run(q)
  }

  val fulltextMatch = SimpleExpression.binary[Seq[String], String, Boolean] { (col, search, qb) =>
    qb.sqlBuilder += "match("
    qb.expr(col)
    qb.sqlBuilder += ") against ("
    qb.expr(search)
    qb.sqlBuilder += " in boolean mode)"
  }

}
