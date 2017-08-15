package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.ExchangeRateСhangeException
import me.peproll.moneytransfer.db.{DatabaseProfile, StorageContext}
import me.peproll.moneytransfer.model.Rate
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait IRateService extends ICommonService[Rate]

final class RateService(override val profile: JdbcProfile)(implicit executionContext: ExecutionContext) extends IRateService
  with StorageContext
  with DatabaseProfile {

  import profile.api._

  override def all: Future[Seq[Rate]] =
    database.run(rates.result)

  override def byId(id: Long): Future[Option[Rate]] =
    database.run(rates.filter(_.id === id).result.headOption)

  override def save(rate: Rate): Future[Int] =
    database.run(rates += rate)

  override def update(rate: Rate): Future[Int] = {
    val action = for {
      exist <- transfers.filter(_.exchangeRate === rate.id).exists.result
      _ = {
        if(exist) throw ExchangeRateСhangeException(rate.id.get)
      }
      aff <- rates.filter(_.id === rate.id).update(rate)
    } yield aff

    database.run(action)
  }


  override def delete(id: Long): Future[Int] = {
    val action = for {
      exist <- transfers.filter(_.exchangeRate === id).exists.result
      _ = {
        if(exist) throw ExchangeRateСhangeException(id)
      }
      aff <- rates.filter(_.id === id).delete
    } yield aff

    database.run(action)
  }
}
