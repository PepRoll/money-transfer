package me.peproll.moneytransfer.db

import me.peproll.moneytransfer.model._
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

final class Helper(override val profile: JdbcProfile)(implicit executionContext: ExecutionContext)
  extends StorageContext with DatabaseProfile {

  import profile.api._

  def setup: Future[Unit] = Future {
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
      )
    ).transactionally

    Await.result(database.run(setup), Duration.Inf)
  }
}