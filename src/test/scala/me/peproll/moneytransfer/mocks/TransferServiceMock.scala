package me.peproll.moneytransfer.mocks

import me.peproll.moneytransfer.model.Transfer
import me.peproll.moneytransfer.services.ITransferService

import scala.concurrent.{ExecutionContext, Future}

class TransferServiceMock(transfers: Seq[Transfer])(implicit executionContext: ExecutionContext)
  extends CommonServiceMock[Transfer](transfers)
    with ITransferService{

  override def transfersByAccountId(accountId: Long) = Future {
    db.filter(_.sourceAccount == accountId)
  }

}
