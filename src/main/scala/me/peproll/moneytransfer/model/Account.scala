package me.peproll.moneytransfer.model

case class Account(id: Option[Long] = None,
                   currency: Currency,
                   balance: BigDecimal,
                   userId: Long,
                   deleted: Boolean = false) extends ID