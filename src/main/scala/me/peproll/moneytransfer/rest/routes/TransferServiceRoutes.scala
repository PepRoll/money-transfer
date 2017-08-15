package me.peproll.moneytransfer.rest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import me.peproll.moneytransfer.{ApplicationContext, ApplicationException}
import me.peproll.moneytransfer.model.Transfer
import me.peproll.moneytransfer.rest.JsonSupport
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

final private[rest] class TransferServiceRoutes(ac: ApplicationContext)(implicit executionContext: ExecutionContext)
  extends JsonSupport {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val routes: Route = pathPrefix("transfers") {
    pathEndOrSingleSlash {
      get {
        val allTransfers = ac.transferService.all
        onComplete(allTransfers) {
          case Success(transfers) => complete(transfers)
          case Failure(ex) =>
            logger.error(ex.getStackTrace.mkString)
            complete(StatusCodes.InternalServerError)
        }
      } ~ post {
        entity(as[Transfer]) { transfer =>
          val createTransfer = ac.transferService.save(transfer)
          onComplete(createTransfer) {
            case Success(1) => complete(StatusCodes.Created -> s"Transfer $transfer has been created")
            case Success(_) => complete(StatusCodes.BadRequest)
            case Failure(ex: ApplicationException) => complete(StatusCodes.BadRequest -> ex.getMessage)
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
            val transferById = ac.transferService.byId(id)
            onComplete(transferById) {
              case Success(Some(transfer)) => complete(transfer)
              case Success(None) => complete(StatusCodes.NotFound -> s"Transfer with ID: $id was not found")
              case Failure(ex) =>
                logger.error(ex.getStackTrace.mkString)
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~
          pathPrefix("rate") {
            pathEndOrSingleSlash {
              get {
                val rateByTransferId = for {
                  transfer <- ac.transferService.byId(id)
                  rateId = for {
                    t <- transfer
                    id <- t.id
                  } yield id
                  if rateId.isDefined
                  r <- ac.rateService.byId(rateId.get)
                } yield r
                onComplete(rateByTransferId) {
                  case Success(rate) => complete(rate)
                  case Failure(ex) =>
                    logger.error(ex.getStackTrace.mkString)
                    complete(StatusCodes.InternalServerError)
                }
              }
            }
          }
      }
  }

}
