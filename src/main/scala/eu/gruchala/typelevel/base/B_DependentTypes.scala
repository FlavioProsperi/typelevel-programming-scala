package eu.gruchala.typelevel.base

object B_DependentTypes {

  val desc =
    """
      |A dependent type is a type that depends on a value.
      |In languages like Java we use types (and define them upfront)
      |to give us information about values and to put constraints on them.
      |With Dependent Types we are more flexible because we can compute types
      |and define stronger constraints on values.
    """.stripMargin


  object PathDependentTypes {

    class Foo {
      class Bar
    }

    val foo1 = new Foo
    val foo2 = new Foo

    //# means any Bar
    val a: Foo#Bar = new foo1.Bar
    val b: Foo#Bar = new foo2.Bar

    // . means Bar from given instance / path
    val c: foo1.Bar = new foo1.Bar
//    val d: foo1.Bar = new foo2.Bar // can't assign foo2.Bar type to foo1.Bar
  }

  object ParameterDependentTypes {

    trait Foo {
      type Bar
      def value: Bar
    }

    def foo(f: Foo): f.Bar = f.value
  }

}
