package eu.gruchala.typelevel.base

import scala.language.{higherKinds, implicitConversions}

object G_SimplifiedCategoryTheory {

  val about = """
    |If we have some type constructor C[_], types A and B, we want to apply functions of type C[A] => C[B]. These are our options:
    |
    |(A => B) => FCA] => C[B]
    |
    |(A => C[B]) => C[A] => C[B]
    |
    |(C[A => B]) => C[A] => C[B]
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
  
  class Basket[T](val content: T)

  object Category {

    //Category implementations for needed types
    implicit object BasketCategory extends Category[Basket] {
      //functor, takes raw function
      override def map[A, B](f: (A) => B): (Basket[A]) => Basket[B] =
        aBasket => new Basket[B] (content = f(aBasket.content))

      //monad, takes semi-raw function
      override def flatMap[A, B](f: (A) => Basket[B]): (Basket[A]) => Basket[B] =
        aBasket => f(aBasket.content)

      //applicative - transformation inside
      override def apply[A, B](f: Basket[(A) => B]): (Basket[A]) => Basket[B] =
        (aBasket: Basket[A]) => new Basket (f.content(aBasket.content))
    }

    //Let's give our category transformations to any type we have category implementation
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

  //We will exchange our Basket from Fruit to its price (Basket[Fruit] => Basket[Price])
  trait Fruit { def name: String }
  trait Price { def value: Double }

  val basketOfFruit: Basket[Fruit] = new Basket[Fruit] (content = new Fruit {def name = "pear"})

  //Functor
  val toPrice: Fruit => Price = fruit => if (fruit.name == "pear") new Price {def value = 2.99 } else new Price {def value = 9.99 }
  val priceOfFruit: Basket[Price] = basketOfFruit.map(toPrice)

  //Monad
  val toPriceInNewBasket: Fruit => Basket[Price] = fruit => new Basket[Price] (content = toPrice(fruit))
  val priceInNewBasket: Basket[Price] = basketOfFruit.flatMap(toPriceInNewBasket)

  //Applicative - (transformation inside)
  //We have a transformation function inside a Basket
  val exchangeBasket: Basket[Fruit => Price] = new Basket(toPrice)
  //and we can apply it on another Basket (exchange one basket to another with change definition inside the latter) 
  val againNewBasketWithPrice: Basket[Price] = basketOfFruit.apply(exchangeBasket)
}
