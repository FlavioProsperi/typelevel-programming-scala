package eu.gruchala.typelevel.base

import scala.language.{higherKinds, implicitConversions}

object G_SimplifiedCategoryTheory {

  val definition =
    """
      |A category is made up from objects and maps (aka morphisms or arrows) between these objects.
      |Maps can be composed in an associative fashion and for each object there is an identity map
      |which is neutral with regard to composition.
      |
      |In terms of Scala, we use Scala types as objects and Scala functions as maps.
    """.stripMargin

  val algebra = """
    |If we have some type constructor C[_] (higher-kind), types A and B, we want to apply functions of type C[A] => C[B]. These are our options:
    |
    |(A => B) => FCA] => C[B]
    |
    |(A => C[B]) => C[A] => C[B]
    |
    |(C[A => B]) => C[A] => C[B]
  """.stripMargin

  trait Category[C[_]] {

    def id[A]: A => A = a => a

    def map[A, B](f: A => B): C[A] => C[B]

    def flatMap[A, B](f: A => C[B]): C[A] => C[B]

    def apply[A, B](f: C[A => B]): C[A] => C[B]
  }
  
  class Basket[T](val content: T)
  trait Fruit { def name: String }
  trait Price { def value: Double }

  object Category {

    implicit object BasketCategory extends Category[Basket] {

      override def map[A, B](f: (A) => B): (Basket[A]) => Basket[B] = ???

      override def flatMap[A, B](f: (A) => Basket[B]): (Basket[A]) => Basket[B] = ???

      override def apply[A, B](f: Basket[(A) => B]): (Basket[A]) => Basket[B] = ???
    }

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

  val basketOfFruit: Basket[Fruit] = new Basket[Fruit] (content = new Fruit {def name = "pear"})

  //Functor
  val toPrice: Fruit => Price = fruit => if (fruit.name == "pear") new Price {def value = 2.99 } else new Price {def value = 9.99 }
  val priceOfFruit: Basket[Price] = ???

  //Monad
  val toPriceInNewBasket: Fruit => Basket[Price] = fruit => new Basket[Price] (content = toPrice(fruit))
  val priceInNewBasket: Basket[Price] = ???

  //Applicative
  val exchangeBasket: Basket[Fruit => Price] = new Basket(toPrice)
  val againNewBasketWithPrice: Basket[Price] = ???
}
