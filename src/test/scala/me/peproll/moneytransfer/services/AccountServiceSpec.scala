package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.model.{Account, EUR, RUB, USD}

class AccountServiceSpec extends BaseServiceSpec {

  val accounts = Seq(
    Account(id = Some(1), currency = RUB, balance = 2000, userId = 1),
    Account(id = Some(2), currency = EUR, balance = 200, userId = 1),
    Account(id = Some(3), currency = USD, balance = 500, userId = 2)
  )

  test("return all accounts") {
    accountService.all.map { result =>
      result shouldEqual accounts
    }
  }

  test("byId return RUB account") {
    accountService.byId(1).map { result =>
      result shouldEqual accounts.headOption
    }
  }

  test("byId return None for unknown account") {
    accountService.byId(999).map { result =>
      result shouldEqual None
    }
  }

  test("byId return None for deleted account") {
    for {
      _ <- accountService.delete(1)
      account <- accountService.byId(1)
    } yield account shouldEqual None
  }

  test("accountsByUserId return Mark's accounts") {
    accountService.accountsByUserId(1) map { result =>
      result shouldEqual accounts.take(2)
    }
  }

  test("update head account - change balance") {
    accountService.update(accounts.head.copy(balance = 300)) map { result =>
      result shouldEqual 1
    }
  }

  test("save new account") {
    accountService.save(accounts.head.copy(id = None)) map { result =>
      result shouldEqual 1
    }
  }

}
