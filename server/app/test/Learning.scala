package test

import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import cats.{Eq, Functor, Monoid, Show}

import scala.concurrent.{Await, Future}
import scala.util.Random

/**
 * Created by Administrator on 2019/11/5
 */

trait Printable[A] {
  self =>
  def format(value: A): String

  //Box=>A  F (Box=>A) F[Box]
  def contramap[B](func: B => A): Printable[B] =
    new Printable[B] {
      def format(value: B): String =
        self.format(func(value))
    }
}

object PrintableInstances {

  implicit val stringPrint = new Printable[String] {
    override def format(value: String): String = {
      value.toString
    }
  }
  implicit val intPrint = new Printable[Int] {
    override def format(value: Int): String = {
      (value + 1).toString
    }
  }

  implicit val catPrint = new Printable[Cat] {
    override def format(c: Cat): String = {
      s"${c.name} is a ${c.age} year-old ${c.color} cat."
    }
  }

}

object Printable {
  def format[A](value: A)(implicit p: Printable[A]): String = {
    p.format(value)
  }

  def print[A](value: A)(implicit p: Printable[A]): Unit = {
    println(format(value))
  }
}

sealed trait Json

final case class JsObject(get: Map[String, Json]) extends Json

final case class JsString(get: String) extends Json

final case class JsNumber(get: Double) extends Json

case object JsNull extends Json

final case class Cat(name: String, age: Int, color: String)

object PrintableSyntax {

  implicit class PrintableOps[A](value: A) {
    def format(implicit p: Printable[A]): String = {
      p.format(value)
    }

    def print(implicit p: Printable[A]): Unit = {
      println(format)
    }

  }

}

trait JsonWriter[-A] {
  def write(value: A): Json
}

sealed trait Shape

case class Circle(radius: Double) extends Shape

case class Order(totalCost: Double, quantity: Double)

object MyInstances {
  implicit val orderAddMonoid = new Monoid[Order] {
    override def empty: Order = Order(0, 0)

    override def combine(x: Order, y: Order): Order = Order(x.totalCost + y.totalCost, x.quantity + y.quantity)
  }
}

sealed trait Tree[+A]

final case class Branch[A](left: Tree[A], right: Tree[A])
  extends Tree[A]

final case class Leaf[A](value: A) extends Tree[A]

object Tree {
  def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
    Branch(left, right)

  def leaf[A](value: A): Tree[A] =
    Leaf(value)
}

final case class Box[A](value: A)


object Learning {

  def main(args: Array[String]): Unit = {
    import cats.instances.int._
    import cats.syntax.show._
    import cats.syntax.eq._
    import cats.syntax.semigroup._
    import cats.syntax.option._
    import cats.instances.int._
    import cats.instances.string._
    import cats.instances.option._
    import cats.instances.list._
    import cats.instances.long._
    import cats.instances.map._
    import cats.instances.tuple._
    import cats.instances.future._
    import cats.Monoid
    import cats.Semigroup
    import cats.instances.function._
    import cats.syntax.functor._

    trait Codec[A] {
      def encode(value: A): String
      def decode(value: String): A
      def imap[B](dec: A => B, enc: B => A): Codec[B] = ???
    }
    def encode[A](value: A)(implicit c: Codec[A]): String =
      c.encode(value)
    def decode[A](value: String)(implicit c: Codec[A]): A =
      c.decode(value)



//    Array(
//
//    ).foreach(println(_))


  }

}
