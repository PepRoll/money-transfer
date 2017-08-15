package me.peproll.moneytransfer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import me.peproll.moneytransfer.db.Helper
import me.peproll.moneytransfer.rest.RestService
import slick.jdbc.H2Profile

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object Main extends App with AppConfig {

  implicit val system = ActorSystem("actor-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val init = new Helper(H2Profile)
  Await.result(init.setup, Duration.Inf)

  val ac = ApplicationContext(H2Profile)
  val rest = new RestService(ac)

  val binding = Http().bindAndHandle(rest.route, httpHost, httpPort)

  println(s"Server http://$httpHost:$httpPort \n Press ENTER to stop...")
  StdIn.readLine()

  binding
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
