package eu.gruchala.typelevel.full

object D_PhantomTypes {

  import Database._

  class Database[S <: State] private {

    //Given CS - current state of Database
    //using type bounds - >: (lower bound - CS must be a super type of S) and <: upper bound (S must be a subtype of State.WriteOnly)
    //def write[T <: Writeable, CS >: S <: State.WriteOnly](data: T): Unit = ()

    //using implicit evidence parameter - compiler must check if S is of the same type as right side: State.WriteOnly
    def write[T <: Writeable](data: T)(implicit ev: S =:= State.WriteOnly): Unit = ()

    def read[T <: Readable[T]](implicit ev: S =:= State.ReadOnly): Readable[T] = new Readable[T] {}
  }

  trait Readable[T]
  trait Writeable
  trait PersonEntity extends Readable[PersonEntity]
  trait Person extends Writeable

  object Database {

    sealed trait State
    object State {
      sealed trait ReadOnly extends State
      sealed trait WriteOnly extends State
    }

    def readOnly: Database[State.ReadOnly] = new Database[State.ReadOnly]
    def writeOnly: Database[State.WriteOnly] = new Database[State.WriteOnly]
  }

  val readableDB = Database.readOnly
  readableDB.read[PersonEntity]
//  readableDB.write(new Person {}) //does not compile

  val writeableDB = Database.writeOnly
//  writeableDB.read[PersonEntity] //does not compile
  writeableDB.write(new Person {})

}
