package me.peproll.moneytransfer.db

import slick.jdbc.JdbcProfile

trait DatabaseProfile {

  val profile: JdbcProfile

  import profile.api._
  protected lazy val database: Database = Database.forConfig("db")
}
