package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class QuestionDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[QuestionRow]) = {
    db.run(Question ++= rows).map(_ => ())
  }

  def insert(row: QuestionRow) = {
    db.run(Question += row).map(_ => ())
  }

  def deleteAll = db.run(Question.delete).map(_ => ())


}
