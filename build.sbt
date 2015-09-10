name := "gameOfLife"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV  = "2.3.10"
  val akkaStreamV = "1.0-RC4"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV withSources() withJavadoc,
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "io.netty" % "netty" % "3.10.4.Final",
    "org.mashupbots.socko" %% "socko-webserver" % "0.6.0"
  )
}