package eu.gruchala.typelevel.base

//object D_PhantomTypes {
//
//  sealed trait Readable
//  sealed trait Writeable
//
//  class Database {
//
//    def write[T <: Writeable](data: T): Unit = ()
//
//    def read[T <: Readable](): Readable = new Readable {}
//  }
//
//  val readableDB = new Database()
//  readableDB.read()
//  readableDB.write(new Writeable {}) //should not compile
//
//  val writeableDB = new Database()
//  readableDB.read() //should not compile
//  writeableDB.write(new Writeable {})
//
//}

object D_PhantomTypes {

  import Database._

  class Database[S <: State] private {

//    def write[T <: Writeable, CS >: S <: State.WriteOnly](data: T): Unit = ()
    def write[T <: Writeable](data: T)(implicit ev: S =:= State.WriteOnly): Unit = ()

    def read[T <: Readable](implicit ev: S =:= State.ReadOnly): Readable = new Readable {}
  }

  object Database {
    sealed trait Readable
    sealed trait Writeable

    sealed trait State
    object State {
      sealed trait ReadOnly extends State
      sealed trait WriteOnly extends State
    }

    def readOnly: Database[State.ReadOnly] = new Database[State.ReadOnly]
    def writeOnly: Database[State.WriteOnly] = new Database[State.WriteOnly]
  }

  val readableDB = Database.readOnly
  readableDB.read
//  readableDB.write(new Writeable {}) //should not compile

  val writeableDB = Database.writeOnly
//  writeableDB.read //should not compile
  writeableDB.write(new Writeable {})

}
