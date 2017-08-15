package me.peproll.moneytransfer.model

case class Rate(id: Option[Long] = None,
                sourceCurrency: Currency,
                targetCurrency: Currency,
                exchangeRate: BigDecimal) extends ID