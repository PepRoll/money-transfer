package me.peproll.moneytransfer.mocks

import me.peproll.moneytransfer.model.Account
import me.peproll.moneytransfer.services.IAccountService

import scala.concurrent.{ExecutionContext, Future}

class AccountServiceMock(accounts: Seq[Account])(implicit executionContext: ExecutionContext)
  extends CommonServiceMock[Account](accounts)
    with IAccountService {

  override def accountsByUserId(userId: Long) = Future {
    db.filter(ac => ac.userId == userId && !ac.deleted)
  }
}
