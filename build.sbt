name := "gameOfLife"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= {
  val akkaV  = "2.3.10"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV withSources() withJavadoc,
    "org.scalatest" %%  "scalatest" % "2.2.4"  % "test"
  )
}