package me.peproll.moneytransfer.rest

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import me.peproll.moneytransfer.ApplicationContext
import me.peproll.moneytransfer.rest.routes.{AccountServiceRoutes, RateServiceRoutes, TransferServiceRoutes, UserServiceRoutes}

import scala.concurrent.ExecutionContext

final class RestService(ac: ApplicationContext)(implicit executionContext: ExecutionContext) {

  private val userRoutes     =  new UserServiceRoutes(ac)
  private val accountRoutes  =  new AccountServiceRoutes(ac)
  private val transferRoutes =  new TransferServiceRoutes(ac)
  private val rateRoutes     =  new RateServiceRoutes(ac)

  val route: Route = {
    pathPrefix("api") {
      userRoutes.route ~ accountRoutes.route ~ transferRoutes.routes ~ rateRoutes.routes
    }
  }

}
