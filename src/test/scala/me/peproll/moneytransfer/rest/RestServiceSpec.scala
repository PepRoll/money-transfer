package me.peproll.moneytransfer.rest

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import me.peproll.moneytransfer.ApplicationContext
import me.peproll.moneytransfer.mocks.{AccountServiceMock, RateServiceMock, TransferServiceMock, UserServiceMock}
import me.peproll.moneytransfer.model._
import org.scalatest.{AsyncWordSpec, Matchers}

class RestServiceSpec extends AsyncWordSpec
  with Matchers
  with ScalatestRouteTest
  with JsonSupport {

  val users = Seq(
    User(Some(0), "Mark", "Hamill"),
    User(Some(1), "Harrison", "Ford"),
    User(Some(2), "Carrie", "Fisher")
  )

  val accounts = Seq(
    Account(Some(0), RUB, 2000, 1),
    Account(Some(1), EUR, 200, 1),
    Account(Some(2), USD, 500, 2)
  )

  val rates = Seq(
    Rate(Some(0), sourceCurrency = RUB, targetCurrency = RUB, exchangeRate = 1),
    Rate(Some(1), sourceCurrency = RUB, targetCurrency = USD, exchangeRate = 1),
    Rate(Some(2), sourceCurrency = RUB, targetCurrency = EUR, exchangeRate = 1),
    Rate(Some(3), sourceCurrency = USD, targetCurrency = USD, exchangeRate = 1),
    Rate(Some(4), sourceCurrency = USD, targetCurrency = RUB, exchangeRate = 1),
    Rate(Some(5), sourceCurrency = USD, targetCurrency = EUR, exchangeRate = 1),
    Rate(Some(6), sourceCurrency = EUR, targetCurrency = RUB, exchangeRate = 1),
    Rate(Some(7), sourceCurrency = EUR, targetCurrency = USD, exchangeRate = 1),
    Rate(Some(8), sourceCurrency = EUR, targetCurrency = EUR, exchangeRate = 1),
  )

  val transfers = Seq(
    Transfer(
      id = Some(0),
      sourceAccount = 1,
      targetAccount = 2,
      amount = 20,
      exchangeRateId = 2),
    Transfer(
      id = Some(1),
      sourceAccount = 2,
      targetAccount = 1,
      amount = 200,
      exchangeRateId = 5)
  )

  val ac: ApplicationContext = new ApplicationContext(
    userService     = new UserServiceMock(users),
    accountService  = new AccountServiceMock(accounts),
    rateService     = new RateServiceMock(rates),
    transferService = new TransferServiceMock(transfers)
  )

  def route: Route = new RestService(ac).route

  "Rest service" should {

    "resource users" should {

      "return users GET /api/users" in {
        Get("/api/users") ~> route ~> check {
          responseAs[Seq[User]] shouldEqual users
        }
      }

      "return user GET /api/users/1" in {
        Get("/api/users/1") ~> route ~> check {
          responseAs[User] shouldEqual users(1)
        }
      }

      "return status code GET /api/users/999" in {
        Get("/api/users/999") ~> route ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }

      "return status code Created POST /api/users" in {
        val newUser = User(firstName = "firstName", lastName = "lastName")
        Post("/api/users", newUser) ~> route ~> check {
          status shouldEqual StatusCodes.Created
        }
      }

      "return status code ResetContent PUT /api/users" in {
        val updatedUser = users.head.copy(firstName = "firstName")
        Put("/api/users", updatedUser) ~> route ~> check {
          status shouldEqual StatusCodes.ResetContent
        }
      }

      "return status code BadRequest PUT /api/users" in {
        val newUser = User(firstName = "firstName", lastName = "lastName")
        Put("/api/users", newUser) ~> route ~> check {
          status shouldEqual StatusCodes.BadRequest
        }
      }

      "return status code Accepted DELETE /api/users" in {
        Delete("/api/users/1") ~> route ~> check {
          status shouldEqual StatusCodes.Accepted
        }
      }

      "return status code BadRequest DELETE /api/users" in {
        Delete("/api/users/999") ~> route ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }

      "return accounts GET /api/users/1/accounts" in {
        Get("/api/users/1/accounts") ~> route ~> check {
          responseAs[Seq[Account]] shouldEqual accounts.filter(_.userId == 1)
        }
      }
    }

    "resource accounts" should {

      "return accounts GET /api/accounts" in {
        Get("/api/accounts") ~> route ~> check {
          responseAs[Seq[Account]] shouldEqual accounts
        }
      }

      "return account GET /api/accounts/1" in {
        Get("/api/accounts/1") ~> route ~> check {
          responseAs[Account] shouldEqual accounts(1)
        }
      }

      "return status code GET /api/accounts/999" in {
        Get("/api/accounts/999") ~> route ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }

      "return status code Created POST /api/accounts" in {
        val newAccount = Account(currency = RUB, userId = 1, balance = 22)
        Post("/api/accounts", newAccount) ~> route ~> check {
          status shouldEqual StatusCodes.Created
        }
      }

      "return status code ResetContent PUT /api/accounts" in {
        val updatedAccount = accounts.head.copy(currency = EUR)
        Put("/api/accounts", updatedAccount) ~> route ~> check {
          status shouldEqual StatusCodes.ResetContent
        }
      }

      "return status code BadRequest PUT /api/accounts" in {
        val newAccount = Account(currency = RUB, userId = 1, balance = 22)
        Put("/api/accounts", newAccount) ~> route ~> check {
          status shouldEqual StatusCodes.BadRequest
        }
      }

      "return status code Accepted DELETE /api/accounts" in {
        Delete("/api/accounts/1") ~> route ~> check {
          status shouldEqual StatusCodes.Accepted
        }
      }

      "return status code BadRequest DELETE /api/accounts" in {
        Delete("/api/accounts/999") ~> route ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }

      "return all transfers by account id GET /api/accounts/1/transfers" in {
        Get("/api/accounts/1/transfers") ~> route ~> check {
          responseAs[Seq[Transfer]] shouldEqual transfers.take(1)
        }
      }
    }

    "resource transfers" should {

      "return transfers GET /api/transfers" in {
        Get("/api/transfers") ~> route ~> check {
          responseAs[Seq[Transfer]] shouldEqual transfers
        }
      }

      "return transfer GET /api/transfers/1" in {
        Get("/api/transfers/1") ~> route ~> check {
          responseAs[Transfer] shouldEqual transfers(1)
        }
      }

      "return status code GET /api/transfers/999" in {
        Get("/api/transfers/999") ~> route ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }

      "return status code Created POST /api/transfers" in {
        val newTransfer = Transfer(
          sourceAccount = 1,
          targetAccount = 2,
          amount = 20,
          exchangeRateId = 3)
        Post("/api/transfers", newTransfer) ~> route ~> check {
          status shouldEqual StatusCodes.Created
        }
      }

      "return rate by transfer id GET /api/transfers/1/rate" in {
        Get("/api/transfers/1/rate") ~> route ~> check {
          responseAs[Option[Rate]] shouldEqual Some(rates(1))
        }
      }

    }

    "resource rates" should {

      "return rates GET /api/rates" in {
        Get("/api/rates") ~> route ~> check {
          responseAs[Seq[Rate]] shouldEqual rates
        }
      }

      "return rate GET /api/rates/1" in {
        Get("/api/rates/1") ~> route ~> check {
          responseAs[Rate] shouldEqual rates(1)
        }
      }

      "return status code NotFound GET /api/rates/999" in {
        Get("/api/rates/999") ~> route ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }

      "return status code Created POST /api/rates" in {
        val newRate = Rate(sourceCurrency = RUB, targetCurrency = RUB, exchangeRate = 1)
        Post("/api/rates", newRate) ~> route ~> check {
          status shouldEqual StatusCodes.Created
        }
      }

      "return status code ResetContent PUT /api/rates" in {
        val updatedRate = rates.head.copy(sourceCurrency = EUR)
        Put("/api/rates", updatedRate) ~> route ~> check {
          status shouldEqual StatusCodes.ResetContent
        }
      }

      "return status code BadRequest PUT /api/rates" in {
        val newRate = Rate(sourceCurrency = RUB, targetCurrency = RUB, exchangeRate = 1)
        Put("/api/rates", newRate) ~> route ~> check {
          status shouldEqual StatusCodes.BadRequest
        }
      }

      "return status code Accepted DELETE /api/rates" in {
        Delete("/api/rates/1") ~> route ~> check {
          status shouldEqual StatusCodes.Accepted
        }
      }

      "return status code BadRequest DELETE /api/rates" in {
        Delete("/api/rates/999") ~> route ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }

    }

  }
}
