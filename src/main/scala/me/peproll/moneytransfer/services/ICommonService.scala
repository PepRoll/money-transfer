package me.peproll.moneytransfer.services

import scala.concurrent.Future

trait ICommonService[E] {
  def all: Future[Seq[E]]
  def byId(id: Long): Future[Option[E]]
  def save(entity: E): Future[Int]
  def update(entity: E): Future[Int]
  def delete(id: Long): Future[Int]
}
