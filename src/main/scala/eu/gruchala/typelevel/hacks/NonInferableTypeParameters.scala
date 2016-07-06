package eu.gruchala.typelevel.hacks

import scalaz._
import Scalaz._
import scala.language.higherKinds

object NonInferableTypeParameters {

  //Thanks to https://tpolecat.github.io/2015/07/30/infer.html

  def wrap[F[_]: Applicative, A](a: A): F[A] =
    Applicative[F].point(a)

  val listOfInt: List[Int] = wrap[List, Int](1) //But `Int` can be inferred easily, whereas `List` not

  //[error]  match expected type scalaz.Applicative[F]
  //wrap(1) // Nothing[Int]

  //Expose only non inferable type and use polymorphic `apply` method for inferable type argument
  final class WrapHelper[F[_]] {
    def apply[A](a: A)(implicit ev: Applicative[F]): F[A] =
      ev.point(a)
  }
  def betterWrap[F[_]] = new WrapHelper[F]

  val betterList: List[Int] = betterWrap[List](1)
  val betterOption: Option[String] = betterWrap[Option]("value")
}
