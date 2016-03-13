package eu.gruchala.typelevel.base

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

  //generic method
  def getValue(b: Box): b.T = b.value


  val stringValue: String = getValue(StringBox)

  val intValue: Int = getValue(IntBox)

  //TODO find some real world useful example

}
