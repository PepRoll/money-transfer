package me.peproll.moneytransfer.rest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import me.peproll.moneytransfer.ApplicationContext
import me.peproll.moneytransfer.model.Account
import me.peproll.moneytransfer.rest.JsonSupport
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

final private[rest] class AccountServiceRoutes(ac: ApplicationContext)(implicit executionContext: ExecutionContext) extends JsonSupport {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val route: Route = pathPrefix("accounts") {
    pathEndOrSingleSlash {
      get {
        val allAccounts = ac.accountService.all
        onComplete(allAccounts) {
          case Success(accounts) => complete(accounts)
          case Failure(ex) =>
            logger.error(ex.getStackTrace.mkString)
            complete(StatusCodes.InternalServerError)
        }
      } ~ post {
        entity(as[Account]) { account =>
          val createAccount = ac.accountService.save(account)
          onComplete(createAccount) {
            case Success(1) => complete(StatusCodes.Created -> s"Account $account has been created")
            case Success(_) => complete(StatusCodes.BadRequest)
            case Failure(ex) =>
              logger.error(ex.getStackTrace.mkString)
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~ put {
        entity(as[Account]) { account =>
          val updateAccount = ac.accountService.update(account)
          onComplete(updateAccount) {
            case Success(1) => complete(StatusCodes.ResetContent -> s"Account with ID: ${account.id} has been updated")
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
            val byId = ac.accountService.byId(id)
            onComplete(byId) {
              case Success(Some(account)) => complete(account)
              case Success(None) => complete(StatusCodes.NotFound -> s"Account with ID: $id was not found")
              case Failure(ex) =>
                logger.error(ex.getStackTrace.mkString)
                complete(StatusCodes.InternalServerError)
            }
          } ~ delete {
            val deleteById = ac.accountService.delete(id)
            onComplete(deleteById) {
              case Success(1) => complete(StatusCodes.Accepted -> s"Account with ID: $id has been deleted")
              case Success(_) => complete(StatusCodes.NotFound -> s"Account with ID: $id was not found")
              case Failure(ex) =>
                logger.error(ex.getStackTrace.mkString)
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~ pathPrefix("transfers") {
          pathEndOrSingleSlash {
            get {
              val transfersByAccountId = ac.transferService.transfersByAccountId(id)
              onComplete(transfersByAccountId) {
                case Success(transfers) => complete(transfers)
                case Failure(ex) =>
                  logger.error(ex.getStackTrace.mkString)
                  complete(StatusCodes.InternalServerError)
              }
            }
          } ~
            pathPrefix(LongNumber) { transferId =>
              pathEndOrSingleSlash {
                get {
                  val transferById = for {
                    t <- ac.transferService.byId(transferId)
                    if t.exists(_.sourceAccount == id)
                  } yield t
                  onComplete(transferById) {
                    case Success(Some(transfer)) => complete(transfer)
                    case Success(None) =>
                      complete(StatusCodes.NotFound -> s"Transfer with ID: $transferId was not found")
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
                        transfer <- ac.transferService.byId(transferId)
                        if transfer.exists(_.sourceAccount == id)
                        rateId = for {
                          t <- transfer
                          id <- t.id
                        } yield id
                        if rateId.isDefined
                        r <- ac.rateService.byId(rateId.get)
                      } yield r
                      onComplete(rateByTransferId) {
                        case Success(Some(rate)) => complete(rate)
                        case Success(None) =>
                          complete(StatusCodes.NotFound -> s"Rate by transfer ID: $transferId was not found")
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
  }

}
