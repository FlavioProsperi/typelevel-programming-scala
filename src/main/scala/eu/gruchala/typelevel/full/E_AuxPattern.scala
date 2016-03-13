package eu.gruchala.typelevel.full

object E_AuxPattern {

  trait Foo[A] {
    type B
    def value: B
  }

  object Foo {

    //Heart of Aux pattern
    type Aux[A0, B0] = Foo[A0] { type B = B0 }

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
  // does not compile, illegal dependent method type: parameter appears in the type of another parameter in the same section or an earlier one
//  def foo[A](a: A)(implicit f: Foo[A], opt: Option[f.B]): f.B = opt.getOrElse(f.value)


  //f is in fact a predicate. We want compiler to prove that within scope exists evidence that A0 can be converted to B0
  //If we inform compiler about type B0, we will know type of B - and vice versa! If we have defined type B, we know type B0!
  def foo[A, R](a: A)(implicit f: Foo.Aux[A, R], opt: Option[R]): R = opt.getOrElse(f.value)

  implicit val optString: Option[String] = Some("asd")
  val a: String = foo(2)

  implicit val optBoolean: Option[Boolean] = Some(false)
  val b: Boolean = foo("")


  //real world scenario using shapeless
  import shapeless._
  import shapeless.ops.hlist.Length
  //Generic will extract generic representation of T and return its type in R, which can be passed to Length
  def length[T, R <: HList](t: T)(implicit g: Generic.Aux[T, R], l: Length[R]): l.Out = l()
  case class HClass(i: Int, s: String, b: Boolean)
  val hClass = HClass(1, "", b = false)
  Nat.toInt(length(hClass)) // 3

}
