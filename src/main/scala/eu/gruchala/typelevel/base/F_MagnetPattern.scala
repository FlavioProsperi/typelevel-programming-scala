package eu.gruchala.typelevel.base

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

object F_MagnetPattern {

  class HttpResponse
  sealed trait StatusCode
  object StatusCode {
    case object Ok extends StatusCode
    case object Bad_Request extends StatusCode
  }

  trait RouteOps {

    //A problem
    def complete(status: StatusCode): Unit
    def complete(response: HttpResponse): Int
//    def complete(future: Future[StatusCode]): Unit //does not compile
    def complete(future: Future[HttpResponse]): Unit
    def complete[T](obj: T): Int
    def complete[T](statusCode: StatusCode, obj: T): Int
  }

  //Magnet pattern is an alternative approach to method overloading. A fix for type erasure and code duplication.

  sealed trait CompletionMagnet {
    type Result
    def apply(): Result
  }
  object CompletionMagnet {
    implicit def fromStatusCode(statusCode: StatusCode): CompletionMagnet =
      new CompletionMagnet {
        override type Result = Int
        override def apply(): Result = 200 //sophisticated impl
      }

    //private[typelevel]
    implicit def fromFutureStatusCode(future: Future[StatusCode]): CompletionMagnet =
      new CompletionMagnet {
        override type Result = Unit
        override def apply(): Result = () //sophisticated impl
      }
    //etc.
  }

  object MagnetRoute {
    import CompletionMagnet._

    def complete(magnet: CompletionMagnet): magnet.Result = magnet()

    val statusResponse: CompletionMagnet#Result = complete(StatusCode.Bad_Request)
    statusResponse.isInstanceOf[Int] // true

    val futureStatusResponse: CompletionMagnet#Result = complete(Future(StatusCode.Ok))
    futureStatusResponse.isInstanceOf[Unit]
  }
}
