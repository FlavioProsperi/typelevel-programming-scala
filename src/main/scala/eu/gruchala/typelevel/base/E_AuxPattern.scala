package eu.gruchala.typelevel.base

object E_AuxPattern {

  trait Foo[A] {
    type B
    def value: B
  }

  object Foo {

    implicit def fooIntToString = new Foo[Int] {
      override type B = String
      override def value: B = "message"
    }

    implicit def fooStringToBoolean = new Foo[String] {
      override type B = Boolean
      override def value: B = true
    }
  }

  def fooSimple[A](a: A)(implicit f: Foo[A]): f.B = f.value
  val simpleString: String = fooSimple(2)
  val simpleBoolean: Boolean = fooSimple("")

  //the problem
//  def foo[A](a: A)(implicit f: Foo[A], opt: Option[f.B]): f.B = opt.getOrElse(f.value)








  //real world scenario using shapeless
  import shapeless._
  import shapeless.ops.hlist.Length
  //Generic will extract generic representation of T and return its type in R, which can be passed to Length
  def length[T, R <: HList](t: T)(implicit g: Generic.Aux[T, R], l: Length[R]): l.Out = l()
  case class HClass(i: Int, s: String, b: Boolean)
  val hClass = HClass(1, "", b = false)
  Nat.toInt(length(hClass)) // 3

}
