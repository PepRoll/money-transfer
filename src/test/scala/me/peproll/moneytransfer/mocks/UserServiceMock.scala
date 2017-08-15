package me.peproll.moneytransfer.mocks

import me.peproll.moneytransfer.model.User
import me.peproll.moneytransfer.services.IUserService

import scala.concurrent.ExecutionContext

class UserServiceMock(users: Seq[User])(implicit executionContext: ExecutionContext)
  extends CommonServiceMock[User](users)
  with IUserService {
}
