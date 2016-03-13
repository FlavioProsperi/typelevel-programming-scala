package eu.gruchala.typelevel.full

object D_PhantomTypes {

  import Database._

  class Database[S <: State] private {

    //Given CS - current state of Database
    //using type bounds - >: (lower bound - CS must be a super type of S) and <: upper bound (S must be a subtype of State.WriteOnly)
    //def write[T <: Writeable, CS >: S <: State.WriteOnly](data: T): Unit = ()

    //using implicit evidence parameter - compiler must check if S is of the same type as right side: State.WriteOnly
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
//  readableDB.write(new Writeable {}) //does not compile

  val writeableDB = Database.writeOnly
//  writeableDB.read //does not compile
  writeableDB.write(new Writeable {})

}
