package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.db.DatabaseHelper
import org.scalatest.{AsyncFunSuite, BeforeAndAfterEach, Matchers}
import slick.jdbc.H2Profile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class BaseServiceSpec extends AsyncFunSuite with Matchers with BeforeAndAfterEach {

  val userService = new UserService(H2Profile)
  val accountService = new AccountService(H2Profile)
  val rateService = new RateService(H2Profile)
  val transferService = new TransferService(H2Profile)

  override protected def beforeEach(): Unit = {
    Await.result(dh.init, Duration.Inf)
  }

  override protected def afterEach(): Unit = {
    Await.result(dh.clean, Duration.Inf)
  }

  private val dh = new DatabaseHelper(H2Profile)
}
