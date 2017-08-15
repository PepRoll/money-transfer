package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.db.{DatabaseProfile, StorageContext}
import me.peproll.moneytransfer.model.Account
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait IAccountService extends ICommonService[Account] {
  def accountsByUserId(userId: Long): Future[Seq[Account]]
}

final class AccountService(override val profile: JdbcProfile) extends IAccountService
  with StorageContext
  with DatabaseProfile {

  import profile.api._

  override def all: Future[Seq[Account]] =
    database.run(accounts.filter(!_.deleted).result)

  override def byId(id: Long): Future[Option[Account]] =
    database.run(accounts.filter(account => account.id === id && !account.deleted).result.headOption)

  override def save(account: Account): Future[Int] =
    database.run(accounts += account)

  override def update(account: Account): Future[Int] = {
    val updateQuery = accounts.filter(e => e.id === account.id && !e.deleted).update(account)
    database.run(updateQuery)
  }

  override def delete(id: Long): Future[Int] = {
    val query = accounts.filter(e => !e.deleted && e.id === id).map(_.deleted).update(true)
    database.run(query)
  }

  override def accountsByUserId(userId: Long): Future[Seq[Account]] = {
    val query = accounts.filter(e => !e.deleted && e.userId === userId).result
    database.run(query)
  }

}
