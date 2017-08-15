package me.peproll.moneytransfer

sealed trait ApplicationException extends Exception

case class InsufficientFundsException(account: Long) extends ApplicationException {
  override def getMessage = s"Insufficient funds on account with ID: $account"
}

case class RelatedEntityException(entityType: String, id: Long) extends ApplicationException {
  override def getMessage = s"Could not find related entity with type: $entityType and id: $id"
}

case class ExchangeRateAccountCurrencyException(exchangeRateId: Long) extends ApplicationException {
  override def getMessage = s"Exchange rate does not correspond currencies of accounts, exchange rate ID: $exchangeRateId"
}

case class ExchangeRateСhangeException(exchangeRateId: Long) extends ApplicationException {
  override def getMessage = s"Could not change exchange rate until it is used, exchange rate ID: $exchangeRateId"
}