package eu.gruchala.typelevel.base

object D_PhantomTypes {

  trait Readable[T]
  trait Writeable
  trait PersonEntity extends Readable[PersonEntity]
  trait Person extends Writeable

  class Database {

    def write[T <: Writeable](data: T): Unit = ()

    def read[T <: Readable[T]]: Readable[T] = new Readable[T] {}
  }

  val readableDB = new Database()
  readableDB.read[PersonEntity]
  readableDB.write(new Person {}) //should not compile

  val writeableDB = new Database()
  writeableDB.read[PersonEntity] //should not compile
  writeableDB.write(new Person {})

}
