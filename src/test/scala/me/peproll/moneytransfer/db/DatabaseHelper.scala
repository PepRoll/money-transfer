package me.peproll.moneytransfer.db
import me.peproll.moneytransfer.model._
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class DatabaseHelper(override val profile: JdbcProfile) extends StorageContext
  with DatabaseProfile {

  import profile.api._

  def init: Future[Unit] = {
    val setup = DBIO.seq(
      (users.schema ++ accounts.schema ++ rates.schema ++ transfers.schema).create,
      rates ++= Seq(
        Rate(sourceCurrency = RUB, targetCurrency = RUB, exchangeRate = 1),
        Rate(sourceCurrency = RUB, targetCurrency = USD, exchangeRate = 0.017),
        Rate(sourceCurrency = RUB, targetCurrency = EUR, exchangeRate = 0.014),
        Rate(sourceCurrency = USD, targetCurrency = USD, exchangeRate = 1),
        Rate(sourceCurrency = USD, targetCurrency = RUB, exchangeRate = 59.83),
        Rate(sourceCurrency = USD, targetCurrency = EUR, exchangeRate = 0.85),
        Rate(sourceCurrency = EUR, targetCurrency = RUB, exchangeRate = 70.72),
        Rate(sourceCurrency = EUR, targetCurrency = USD, exchangeRate = 1.18),
        Rate(sourceCurrency = EUR, targetCurrency = EUR, exchangeRate = 1),
      ),
      users ++= Seq(
        User(firstName = "Mark", lastName = "Hamill"),
        User(firstName = "Harrison", lastName = "Ford"),
        User(firstName = "Carrie", lastName = "Fisher")
      ),
      accounts ++= Seq(
        Account(currency = RUB, balance = 2000, userId = 1),
        Account(currency = EUR, balance = 200, userId = 1),
        Account(currency = USD, balance = 500, userId = 2)
      ),
      transfers ++= Seq(
        Transfer(
          sourceAccount = 1,
          targetAccount = 2,
          amount = 20,
          exchangeRateId = 3),
        Transfer(
          sourceAccount = 3,
          targetAccount = 2,
          amount = 200,
          exchangeRateId = 6)
      )
    ).transactionally
    database.run(setup)
  }

  def clean: Future[Unit] = {
    val clean = (transfers.schema ++ rates.schema ++ accounts.schema ++ users.schema).drop
    database.run(clean.transactionally)
  }

}