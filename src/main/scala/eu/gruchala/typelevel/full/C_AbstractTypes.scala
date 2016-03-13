package eu.gruchala.typelevel.full

object C_AbstractTypes {

  trait Box {
    type T
    def value: T
  }

  object StringBox extends Box {
    override type T = String
    override def value = "message"
  }

  object IntBox extends Box {
    override type T = Int
    override def value = 11
  }

  //given methods which takes a Box object, we can return different types
  def getValue(b: Box): b.T = b.value


  val stringValue: String = getValue(StringBox)

  val intValue: Int = getValue(IntBox)
}
