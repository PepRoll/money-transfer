package me.peproll.moneytransfer.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import me.peproll.moneytransfer.model._
import spray.json._

private[rest] trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object CurrencyJsonFormat extends RootJsonFormat[Currency] {
    override def read(json: JsValue): Currency = json match {
      case JsString("RUB") => RUB
      case JsString("EUR") => EUR
      case JsString("USD") => USD
      case raw => deserializationError(s"Cannot deserialize Currency. Raw input: $raw")
    }

    override def write(obj: Currency): JsValue = JsString(
      obj match {
        case RUB => "RUB"
        case EUR => "EUR"
        case USD => "USD"
      })
  }

  implicit val userFormat: RootJsonFormat[User] = new RootJsonFormat[User] {

    override def read(json: JsValue): User = {
      val jsObject = json.asJsObject

      jsObject.getFields("firstName", "lastName") match {
        case Seq(firstName, lastName) => User(
          id = jsObject.fields.get("id").map(_.convertTo[Long]),
          firstName = firstName.convertTo[String],
          lastName = lastName.convertTo[String]
        )
        case raw => deserializationError(s"Cannot deserialize User. Raw input: $raw")
      }
    }
    override def write(obj: User): JsValue = JsObject(
      List(
        obj.id.map("id" -> _.toJson),
        Some("firstName" -> obj.firstName.toJson),
        Some("lastName" -> obj.lastName.toJson)
      ).flatten:_*
    )
  }
  implicit val accountFormat: RootJsonFormat[Account] = new RootJsonFormat[Account] {
    override def read(json: JsValue): Account = {
      val jsObject = json.asJsObject
      jsObject.getFields("currency", "balance", "userId") match {
        case Seq(currency, balance, userId) => Account(
          id = jsObject.fields.get("id").map(_.convertTo[Long]),
          currency = currency.convertTo[Currency],
          balance = balance.convertTo[BigDecimal],
          userId = userId.convertTo[Long]
        )
        case raw => deserializationError(s"Cannot deserialize Account. Raw input: $raw")
      }
    }
    override def write(obj: Account): JsValue = JsObject(
      List(
        obj.id.map("id" -> _.toJson),
        Some("currency" -> obj.currency.toJson),
        Some("balance" -> obj.balance.toJson),
        Some("userId" -> obj.userId.toJson)
      ).flatten:_*
    )
  }
  implicit val rateFormat: RootJsonFormat[Rate] = jsonFormat4(Rate)
  implicit val transferFormat: RootJsonFormat[Transfer] = jsonFormat5(Transfer)
}
