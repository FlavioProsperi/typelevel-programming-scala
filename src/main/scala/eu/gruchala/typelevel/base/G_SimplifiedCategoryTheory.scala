package eu.gruchala.typelevel.base

import scala.language.{higherKinds, implicitConversions}

object G_SimplifiedCategoryTheory {

  val about = """
    |If we have some type constructor C[_], types A and B, we want to apply functions of type C[A] => C[B]. These are our options:
    |
    |(A => B) => FCA] => C[B] - functor (transformation function of arity-1)
    |
    |(A => C[B]) => C[A] => C[B] - monad (a flatMap function)
    |
    |(C[A => B]) => C[A] => C[B] - applicative (transformation inside the container), BTW applicative functor is a broader concept
  """.stripMargin

  //Generic definition to build functor for any type
  trait Category[C[_]] {

    //functor, takes raw function
    def map[A, B](f: A => B): C[A] => C[B]

    //monad, takes semi-raw function
    def flatMap[A, B](f: A => C[B]): C[A] => C[B]

    //applicative - transformation inside
    def apply[A, B](f: C[A => B]): C[A] => C[B]
  }

  class Box[T](val value: T)

  object Category {

    //Category implementations for needed types
    implicit object BoxCategory extends Category[Box] {
      //functor, takes raw function
      override def map[A, B](f: (A) => B): (Box[A]) => Box[B] =
        aBox => new Box[B] (value = f(aBox.value))

      //monad, takes semi-raw function
      override def flatMap[A, B](f: (A) => Box[B]): (Box[A]) => Box[B] =
        aBox => f(aBox.value)

      //applicative - transformation inside
      override def apply[A, B](f: Box[(A) => B]): (Box[A]) => Box[B] =
        (aBox: Box[A]) => new Box (f.value(aBox.value))
    }

    //Let's give our category transformations to any type we have our category implementation
    implicit class EnrichWithCategory[C[_], A](c: C[A]) {
      def map[B](f: A => B)(implicit functor: Category[C]): C[B] =
        functor.map(f)(c)

      def flatMap[B](f: A => C[B])(implicit monad: Category[C]): C[B] =
        monad.flatMap(f)(c)

      def apply[B](f: C[A => B])(implicit applicative: Category[C]): C[B] =
        applicative.apply(f)(c)
    }
  }

  import Category._

  val stringBox: Box[String] = new Box[String] (value = "stringbox")

  //Functor
  val length: String => Int = s => s.length

  val intBox: Box[Int] = stringBox.map(length)

  //Monad
  val lengthOfBox: String => Box[Int] = s => new Box[Int] (value = s.length)
  val monadIntBox: Box[Int] = stringBox.flatMap(lengthOfBox)

  //Applicative - (transformation inside)
  //So we have a transformation function inside a Box
  val boxedLength: Box[String => Int] = new Box(length)
  //and we can apply it to another Box
  val applicativeIntBox: Box[Int] = stringBox.apply(boxedLength)

}
