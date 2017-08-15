package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.ExchangeRateСhangeException
import me.peproll.moneytransfer.model.{EUR, RUB, Rate, USD}

class RateServiceSpec extends BaseServiceSpec {

  val rates = Seq(
    Rate(Some(1), sourceCurrency = RUB, targetCurrency = RUB, exchangeRate = 1),
    Rate(Some(2), sourceCurrency = RUB, targetCurrency = USD, exchangeRate = 0.017),
    Rate(Some(3), sourceCurrency = RUB, targetCurrency = EUR, exchangeRate = 0.014),
    Rate(Some(4), sourceCurrency = USD, targetCurrency = USD, exchangeRate = 1),
    Rate(Some(5), sourceCurrency = USD, targetCurrency = RUB, exchangeRate = 59.83),
    Rate(Some(6), sourceCurrency = USD, targetCurrency = EUR, exchangeRate = 0.85),
    Rate(Some(7), sourceCurrency = EUR, targetCurrency = RUB, exchangeRate = 70.72),
    Rate(Some(8), sourceCurrency = EUR, targetCurrency = USD, exchangeRate = 1.18),
    Rate(Some(9), sourceCurrency = EUR, targetCurrency = EUR, exchangeRate = 1),
  )

  test("return all rates") {
    rateService.all.map { result =>
      result shouldEqual rates
    }
  }

  test("byId return RUB -> RUB") {
    rateService.byId(1).map { result =>
      result shouldEqual rates.headOption
    }
  }

  test("byId return None for unknown rate") {
    rateService.byId(999).map { result =>
      result shouldEqual None
    }
  }

  test("byId return None for deleted rate") {
    for {
      _ <- rateService.delete(4)
      account <- rateService.byId(4)
    } yield account shouldEqual None
  }

  test("delete rate whose in use") {
    recoverToSucceededIf[ExchangeRateСhangeException] {
      rateService.delete(3)
    }
  }

  test("update rate whose in use") {
    recoverToSucceededIf[ExchangeRateСhangeException] {
      rateService.update(rates(5).copy(sourceCurrency = EUR))
    }
  }

  test("update head rate - change source currency") {
    rateService.update(rates.head.copy(sourceCurrency = EUR)) map { result =>
      result shouldEqual 1
    }
  }

  test("save new rate") {
    rateService.save(rates.head.copy(id = None)) map { result =>
      result shouldEqual 1
    }
  }

}
