package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.db.{DatabaseProfile, StorageContext}
import me.peproll.moneytransfer.model.User
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait IUserService extends ICommonService[User]

final class UserService(override val profile: JdbcProfile)(implicit executionContext: ExecutionContext) extends IUserService
  with StorageContext
  with DatabaseProfile {

  import profile.api._

  override def all: Future[Seq[User]] =
    database.run(users.filter(!_.deleted).result)

  override def byId(id: Long): Future[Option[User]] =
    database.run(users.filter(user => user.id === id && !user.deleted).result.headOption)

  override def save(user: User): Future[Int] =
    database.run(users += user)

  override def update(user: User): Future[Int] = {
    val updateQuery = users.filter(e => e.id === user.id && !e.deleted).update(user)
    database.run(updateQuery)
  }

  override def delete(id: Long): Future[Int] = {
    val query = for {
      _ <- accounts.filter(ac => !ac.deleted && ac.userId === id).map(_.deleted).update(true)
      c <- users.filter(ac => !ac.deleted && ac.id === id).map(_.deleted).update(true)
    } yield c
    database.run(query.transactionally)
  }
}
