name := "money-transfer"

organization := "me.peproll"

version := "0.1"

scalaVersion := "2.12.3"

scalacOptions := Seq(
  "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Xfatal-warnings",
    "-language:higherKinds",
    "-Yno-adapted-args",
    "-Ywarn-value-discard"
)

parallelExecution in Test := false

libraryDependencies ++= {
  val slickVersion = "3.2.1"
  val akkaHttpVersion = "10.0.9"

  List(
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,

    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.h2database" % "h2" % "1.4.187",

    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,

    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
}