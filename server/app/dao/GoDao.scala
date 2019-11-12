package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.jdbc.JdbcProfile
import tool.HDGSearchData

import scala.concurrent.Future


/**
  * Created by yz on 2019/1/9
  */
class GoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[GoRow]): Future[Unit] = {
    db.run(Go ++= rows).map(_ => ())
  }

  def deleteAll: Future[Unit] = db.run(Go.delete).map(_ => ())

  def selectAll: Future[Seq[GoRow]] = db.run(Go.result)

  def selectAll(kinds:Seq[String])= db.run(Go.filter(_.kind.inSetBind(kinds)).result)

  def selectAll(kinds:Seq[String],species:Seq[String])= db.run(Go.
    filter(_.kind.inSetBind(kinds)).filter(_.species.inSetBind(species)).result)

  def selectByKind(kind: String): Future[Seq[GoRow]] = db.run(Go.filter(_.kind === kind).result)

}
