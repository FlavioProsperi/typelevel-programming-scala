package eu.gruchala.typelevel.base

object B_DependentTypes {

  val ? = "What is a dependant type?"

  object PathDependentTypes {

    class Foo {
      class Bar
    }

    val foo1 = new Foo
    val foo2 = new Foo

  }

  object ParameterDependentTypes {

    trait Foo {
      type Bar
      def value: Bar
    }

  }

}
