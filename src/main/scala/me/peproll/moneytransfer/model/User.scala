package me.peproll.moneytransfer.model

case class User(id: Option[Long] = None,
                firstName: String,
                lastName: String,
                deleted: Boolean = false) extends ID