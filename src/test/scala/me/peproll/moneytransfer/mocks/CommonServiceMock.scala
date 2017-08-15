package me.peproll.moneytransfer.mocks

import me.peproll.moneytransfer.model.ID
import me.peproll.moneytransfer.services.ICommonService

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

class CommonServiceMock[E <: ID](entities: Seq[E])(implicit executionContext: ExecutionContext) extends ICommonService[E] {

  protected val db: ArrayBuffer[E] = ArrayBuffer(entities:_*)

  override def all: Future[Seq[E]] = Future(db)

  override def byId(id: Long) = Future {
    db.find(_.id.exists(_ == id))
  }

  override def save(entity: E) = Future {
    db += entity
    1
  }

  override def update(entity: E) = Future {
    entity.id match {
      case Some(id) =>
        val elIndex = db.indexWhere(_.id.exists(_ == id))
        if (elIndex != -1) {
          db.update(elIndex, entity)
          1
        } else -1
      case _ => -1
    }
  }

  override def delete(id: Long) = Future {
    val elIndex = db.indexWhere(_.id.exists(_ == id))
    if(elIndex != -1) {
      db.remove(elIndex)
      1
    } else -1
  }
}
