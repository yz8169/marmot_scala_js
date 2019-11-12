package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.jdbc.JdbcProfile


/**
 * Created by yz on 2019/1/9
 */
class OtherOrthologsDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[OtherOrthologsRow]) = {
    db.run(OtherOrthologs ++= rows).map(_ => ())
  }

  def deleteAll = db.run(OtherOrthologs.delete).map(_ => ())

  def selectAll = db.run(OtherOrthologs.result)

  def selectByGeneId(geneId: String) = db.run(OtherOrthologs.filter(_.himalayan === geneId).result)

  def selectByGeneIdO(geneId: String) = {
    val t1 = OtherOrthologs.filter(_.himalayan === geneId).result.headOption
    val db1 = db.run(t1)
    val t2 = OtherOrthologs.filter(_.yellow === geneId).result.headOption
    val db2 = db.run(t2)
    val t3 = OtherOrthologs.filter(_.alpine === geneId).result.headOption
    val db3 = db.run(t3)
    val t = Array(db1, db2, db3).reduceLeft { (x, y) =>
      x.zip(y).map { case (o1, o2) =>
        if (o1.isDefined) o1 else o2
      }
    }
    t
  }

}
