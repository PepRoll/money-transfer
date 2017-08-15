package me.peproll.moneytransfer

import me.peproll.moneytransfer.services._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

final class ApplicationContext(val userService: IUserService,
                               val rateService: IRateService,
                               val accountService: IAccountService,
                               val transferService: ITransferService)

object ApplicationContext {
  def apply(profile: JdbcProfile)(implicit executionContext: ExecutionContext): ApplicationContext =
    new ApplicationContext(
      userService     = new UserService(profile),
      rateService     = new RateService(profile),
      accountService  = new AccountService(profile),
      transferService = new TransferService(profile)
    )
}