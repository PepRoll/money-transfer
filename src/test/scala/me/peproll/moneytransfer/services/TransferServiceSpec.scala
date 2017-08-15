package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.model.Transfer
import me.peproll.moneytransfer.{ExchangeRateAccountCurrencyException, InsufficientFundsException, RelatedEntityException}

class TransferServiceSpec extends BaseServiceSpec {

  val transfers = Seq(
    Transfer(
      id = Some(1),
      sourceAccount = 1,
      targetAccount = 2,
      amount = 20,
      exchangeRateId = 3),
    Transfer(
      id = Some(2),
      sourceAccount = 3,
      targetAccount = 2,
      amount = 200,
      exchangeRateId = 6)
  )

  test("return all transfers") {
    transferService.all map { result =>
      result shouldEqual transfers
    }
  }

  test("return transfer with id: 1") {
    transferService.byId(1) map { result =>
      result shouldEqual Some(transfers.head)
    }
  }

  test("return None with unknown id") {
    transferService.byId(999) map { result =>
      result shouldEqual None
    }
  }

  test("return transfers by account id") {
    transferService.transfersByAccountId(1) map { result =>
      result shouldEqual Vector(transfers.head)
    }
  }

  test("create transfer - success") {
    for {
      _ <- transferService.save(Transfer(None, 1, 2, 400, 3))
      sourceAccount <- accountService.byId(1)
      targetAccount <- accountService.byId(2)
    } yield {
      (sourceAccount.map(_.balance), targetAccount.map(_.balance)) shouldEqual (Some(1600.00) -> Some(205.60))
    }
  }

  test("create transfer - exchange rate not correspond currencies of accounts") {
    recoverToSucceededIf[ExchangeRateAccountCurrencyException] {
      transferService.save(Transfer(None, 1, 2, 400, 4))
    }
  }

  test("create transfer - unknown source account") {
    recoverToSucceededIf[RelatedEntityException] {
      transferService.save(Transfer(None, 100, 2, 400, 3))
    }
  }

  test("create transfer - insufficient funds") {
    recoverToSucceededIf[InsufficientFundsException] {
      transferService.save(Transfer(None, 1, 2, 10400, 3))
    }
  }

  test("create transfer with 0 amount") {
    recoverToSucceededIf[IllegalArgumentException] {
      transferService.save(Transfer(None, 1, 2, 0, 3))
    }
  }

  test("create transfer with sourceAccount equal targetAccount") {
    recoverToSucceededIf[IllegalArgumentException] {
      transferService.save(Transfer(None, 1, 1, 400, 3))
    }
  }

  test("update transfer is unsupported operation") {
    recoverToSucceededIf[UnsupportedOperationException] {
      transferService.update(Transfer(
      id = Some(2),
      sourceAccount = 3,
      targetAccount = 2,
      amount = 200,
      exchangeRateId = 4))
    }
  }

  test("delete transfer is unsupported operation") {
    recoverToSucceededIf[UnsupportedOperationException] {
      transferService.delete(2)
    }
  }

}
