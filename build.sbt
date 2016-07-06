name := "typelevel-programming"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.2.5",
  "com.lihaoyi" % "ammonite-repl" % "0.6.2" % "test" cross CrossVersion.full,
  "org.scalaz" %% "scalaz-core" % "7.2.4"
)

initialCommands in (Test, console) := """ammonite.repl.Main.run("")"""

