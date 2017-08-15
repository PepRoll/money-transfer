package me.peproll.moneytransfer.rest.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import me.peproll.moneytransfer.ApplicationContext
import me.peproll.moneytransfer.model.User
import me.peproll.moneytransfer.rest.JsonSupport
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

final private[rest] class UserServiceRoutes(ac: ApplicationContext)(implicit executionContext: ExecutionContext) extends JsonSupport {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val route: Route = pathPrefix("users") {
    pathEndOrSingleSlash {
      get {
        val allUsers = ac.userService.all
        onComplete(allUsers) {
          case Success(users) => complete(users)
          case Failure(ex) =>
            logger.error(ex.getStackTrace.mkString)
            complete(StatusCodes.InternalServerError)
        }
      } ~ post {
        entity(as[User]) { user =>
          val savedUser = ac.userService.save(user)
          onComplete(savedUser) {
            case Success(1) => complete(StatusCodes.Created -> s"User $user has been created")
            case Success(_) => complete(StatusCodes.BadRequest)
            case Failure(ex) =>
              logger.error(ex.getStackTrace.mkString)
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~ put {
        entity(as[User]) { user =>
          val updatedUser = ac.userService.update(user)
          onComplete(updatedUser) {
            case Success(1) => complete(StatusCodes.ResetContent -> s"User with ID: ${user.id} has been updated")
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
            val byId = ac.userService.byId(id)
            onComplete(byId) {
              case Success(Some(user)) => complete(user)
              case Success(None) => complete(StatusCodes.NotFound -> s"User with ID: $id was not found")
              case Failure(ex) =>
                logger.error(ex.getStackTrace.mkString)
                complete(StatusCodes.InternalServerError)
            }
          } ~ delete {
            val deleteById = ac.userService.delete(id)
            onComplete(deleteById) {
              case Success(1) => complete(StatusCodes.Accepted -> s"User with ID: $id has been deleted")
              case Success(_) => complete(StatusCodes.NotFound -> s"User with ID: $id was not found")
              case Failure(ex) =>
                logger.error(ex.getStackTrace.mkString)
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~ pathPrefix("accounts") {
          pathEndOrSingleSlash {
            get {
              val accountsByUserId = ac.accountService.accountsByUserId(id)
              onComplete(accountsByUserId) {
                case Success(accounts) => complete(accounts)
                case Failure(ex) =>
                  logger.error(ex.getStackTrace.mkString)
                  complete(StatusCodes.InternalServerError)
              }
            }
          } ~
            pathPrefix(LongNumber) { accountId =>
              pathEndOrSingleSlash {
                get {
                  val accountById = for {
                    account <- ac.accountService.byId(accountId)
                    if account.exists(_.userId == id)
                  } yield account
                  onComplete(accountById) {
                    case Success(Some(account)) => complete(account)
                    case Success(None) =>
                      complete(StatusCodes.NotFound -> s"Account with ID: $accountId was not found")
                    case Failure(ex) =>
                      logger.error(ex.getStackTrace.mkString)
                      complete(StatusCodes.InternalServerError)
                  }
                }
              } ~
                pathPrefix("transfers") {
                  pathEndOrSingleSlash {
                    get {
                      val transfersByAccountId = for {
                        account <- ac.accountService.byId(accountId)
                        if account.exists(_.userId == id)
                        t <- ac.transferService.transfersByAccountId(accountId)
                      } yield t
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
                            account <- ac.accountService.byId(accountId)
                            if account.exists(_.userId == id)
                            t <- ac.transferService.byId(transferId)
                            if t.exists(_.sourceAccount == accountId)
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
                                account <- ac.accountService.byId(accountId)
                                if account.exists(_.userId == id)
                                transfer <- ac.transferService.byId(transferId)
                                if transfer.exists(_.sourceAccount == accountId)
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
  }
}
