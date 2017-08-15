package me.peproll.moneytransfer.model

case class Transfer(id: Option[Long] = None,
                    sourceAccount: Long,
                    targetAccount: Long,
                    amount: BigDecimal,
                    exchangeRateId: Long) extends ID
