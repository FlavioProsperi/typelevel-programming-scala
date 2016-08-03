package eu.gruchala.typelevel.hacks

import scala.concurrent.{ExecutionContext, Future}

object DoNotStickToParticularMonad {

  //Thanks to http://rea.tech/the-worst-thing-in-our-scala-code-futures/

  //Sticking with Future - harder to test and what if we want to change later to something else (yeah, unlikely)?
  def computation(name: String): Future[String] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    for {
      result <- Future {
        //long computation here...
        s"My name is $name"
      }
    } yield result
  }

  //Define abstract Monad-like type (remember Future has flatMap so it's a Monad, perfect for for-comp)
  import scala.language.higherKinds
  trait MonadLike[F[_]] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
    def map[A, B](fa: F[A])(f: A => B): F[B]
    def pure[A](a: A): F[A]
  }

  //define our implicit typeclass Monad[F], let compiler look for Monad which can handle some F type
  def abstractComputation[F[_]: MonadLike](name: String): F[String] = {
    //let's have some Monad to invoke for this example
    val monad = implicitly[MonadLike[F]]
    monad.pure(name)
  }

  object InTests {

    type Id[A] = A //identity type, clever way to return itself in a typed manner, see also scalaz.Id
    implicit def immediateMonad: MonadLike[Id] = new MonadLike[Id] {
      override def flatMap[A, B](fa: Id[A])(f: (A) => Id[B]): Id[B] = f(fa)
      override def map[A, B](fa: Id[A])(f: (A) => B): Id[B] = f(fa)
      override def pure[A](a: A): Id[A] = a
    }

    abstractComputation("Leszek")
  }

  object InProduction {

    import scala.concurrent.ExecutionContext.Implicits.global
    implicit def futureMonad(implicit ex: ExecutionContext): MonadLike[Future] = new MonadLike[Future] {
      override def flatMap[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa.flatMap(f)
      override def map[A, B](fa: Future[A])(f: (A) => B): Future[B] = fa.map(f)
      override def pure[A](a: A): Future[A] = Future.successful(a)
    }

    abstractComputation("Leszek")
  }


  //Future is not a strict Monad. It has monadic properties, but does not comply with all 3 Monad laws.
  //Futures are not Monads because they have side effects, whereas Monad contain a value.
  //Left identity is broken: Try(x) flatMap f == f(x)` will fail if `f` throws an exception
  //Scala'z Task is like Future but it holds Monad laws and postpones effects till you actually run it (sync or async)
  //https://wiki.haskell.org/Monad_laws
  //http://stackoverflow.com/questions/27454798/is-future-in-scala-a-monad
}
