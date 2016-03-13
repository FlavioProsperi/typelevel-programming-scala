package eu.gruchala.typelevel.base

object D_PhantomTypes {

  sealed trait Readable
  sealed trait Writeable

  class Database {

    def write[T <: Writeable](data: T): Unit = ()

    def read[T <: Readable]: Readable = new Readable {}
  }

  val readableDB = new Database()
  readableDB.read
  readableDB.write(new Writeable {}) //should not compile

  val writeableDB = new Database()
  readableDB.read //should not compile
  writeableDB.write(new Writeable {})

}
