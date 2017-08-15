package me.peproll.moneytransfer

import com.typesafe.config.ConfigFactory

trait AppConfig {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")

  val httpHost: String = httpConfig.getString("host")
  val httpPort: Int = httpConfig.getInt("port")
}
