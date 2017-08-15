package me.peproll.moneytransfer.rest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import me.peproll.moneytransfer.ApplicationContext
import me.peproll.moneytransfer.model.Rate
import me.peproll.moneytransfer.rest.JsonSupport
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

final private[rest] class RateServiceRoutes(ac: ApplicationContext) extends JsonSupport {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val routes: Route = pathPrefix("rates") {
    pathEndOrSingleSlash {
      get {
        val allRates = ac.rateService.all
        onComplete(allRates) {
          case Success(rates) => complete(rates)
          case Failure(ex) =>
            logger.error(ex.getStackTrace.mkString)
            complete(StatusCodes.InternalServerError)
        }
      } ~
        post {
          entity(as[Rate]) { rate =>
            val createRate = ac.rateService.save(rate)
            onComplete(createRate) {
              case Success(1) => complete(StatusCodes.Created -> s"Rate $rate has been created")
              case Success(_) => complete(StatusCodes.BadRequest)
              case Failure(ex) =>
                logger.error(ex.getStackTrace.mkString)
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~
        put {
          entity(as[Rate]) { rate =>
            val updatedRate = ac.rateService.update(rate)
            onComplete(updatedRate) {
              case Success(1) => complete(StatusCodes.ResetContent -> s"Rate with ID: ${rate.id} has been updated")
              case Success(_) => complete(StatusCodes.BadRequest)
              case Failure(ex) =>
                logger.error(ex.getStackTrace.mkString)
                complete(StatusCodes.InternalServerError)
            }
          }
        }
    } ~
      pathPrefix(LongNumber) { id =>
        pathEndOrSingleSlash {
          get {
            val byId = ac.rateService.byId(id)
            onComplete(byId) {
              case Success(Some(rate)) => complete(rate)
              case Success(None) => complete(StatusCodes.NotFound -> s"Rate with ID: $id was not found")
              case Failure(ex) =>
                logger.error(ex.getStackTrace.mkString)
                complete(StatusCodes.InternalServerError)
            }
          } ~
            delete {
              val deleteById = ac.rateService.delete(id)
              onComplete(deleteById) {
                case Success(1) => complete(StatusCodes.Accepted -> s"Rate with ID: $id has been deleted")
                case Success(_) => complete(StatusCodes.NotFound -> s"Rate with ID: $id was not found")
                case Failure(ex) =>
                  logger.error(ex.getStackTrace.mkString)
                  complete(StatusCodes.InternalServerError)
              }
            }
        }
      }
  }


}
