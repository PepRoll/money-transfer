package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.db.{DatabaseProfile, StorageContext}
import me.peproll.moneytransfer.model.Transfer
import me.peproll.moneytransfer.{ExchangeRateAccountCurrencyException, InsufficientFundsException, RelatedEntityException}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait ITransferService extends ICommonService[Transfer] {
  def transfersByAccountId(accountId: Long): Future[Seq[Transfer]]
}

final class TransferService(override val profile: JdbcProfile)(implicit executionContext: ExecutionContext)
  extends ITransferService
    with StorageContext
    with DatabaseProfile {

  import profile.api._

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def all: Future[Seq[Transfer]] =
    database.run(transfers.result)

  override def byId(id: Long): Future[Option[Transfer]] =
    database.run(transfers.filter(_.id === id).result.headOption)

  override def save(transfer: Transfer): Future[Int] = {

    val rateQuery = rates.filter(_.id === transfer.exchangeRateId)
    val sourceAccountQuery = accounts.filter(a => a.id === transfer.sourceAccount && !a.deleted)
    val targetAccountQuery = accounts.filter(a => a.id === transfer.targetAccount && !a.deleted)

    def action: DBIO[Int] = for {

      rOpt <- rateQuery.result.headOption
      sOpt <- sourceAccountQuery.result.headOption
      tOpt <- targetAccountQuery.result.headOption


      // validating
      (rate, sourceAccount, targetAccount) = {
        val r = rOpt.getOrElse(throw RelatedEntityException("rate", transfer.exchangeRateId))
        val s = sOpt.getOrElse(throw RelatedEntityException("account", transfer.sourceAccount))
        val t = tOpt.getOrElse(throw RelatedEntityException("account", transfer.targetAccount))

        if (r.sourceCurrency != s.currency || r.targetCurrency != t.currency)
          throw ExchangeRateAccountCurrencyException(transfer.exchangeRateId)

        if (s.balance < transfer.amount) throw InsufficientFundsException(transfer.sourceAccount)

        (r, s, t)
      }

      // update accounts
      sourceUpdate <- accounts
        .filter(_.id === transfer.sourceAccount)
        .map(_.balance)
        .update(sourceAccount.balance - transfer.amount)
      _ = logger.debug(s"Affected rows after update balance for source account $sourceUpdate")

      targetUpdate <- accounts
        .filter(_.id === transfer.targetAccount)
        .map(_.balance)
        .update(targetAccount.balance + (transfer.amount * rate.exchangeRate))
      _ = logger.debug(s"Affected rows after update balance for target account $targetUpdate")

      // save transfer
      aff <- transfers += transfer
    } yield aff

    def validate = Future{
      require(transfer.amount > 0, "Transfer amount must be more than 0")
      require(transfer.sourceAccount != transfer.targetAccount, "Transfer's account must be different")
    }

    for {
      _ <- validate
      a <- database.run(action.transactionally)
    } yield a
  }

  override def update(transfer: Transfer): Future[Int] =
    Future.failed(new UnsupportedOperationException("Operation update is unsupported for transfer"))

  override def delete(id: Long): Future[Int] = {
    Future.failed(new UnsupportedOperationException("Operation delete is unsupported for transfer"))
  }

  override def transfersByAccountId(accountId: Long): Future[Seq[Transfer]] = {
    val query = transfers join accounts on {
      case (t, a) =>
        t.sourceAccount === a.id &&
        !a.deleted &&
        a.id === accountId
    } map(_._1)
    database.run(query.result)
  }
}
