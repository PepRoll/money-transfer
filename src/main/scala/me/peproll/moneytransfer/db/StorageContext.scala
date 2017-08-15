package me.peproll.moneytransfer.db

import me.peproll.moneytransfer.model._
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType

trait StorageContext { self: DatabaseProfile =>
  import profile.api._

  protected lazy val users = TableQuery[Users]
  protected lazy val rates = TableQuery[Rates]
  protected lazy val accounts = TableQuery[Accounts]
  protected lazy val transfers = TableQuery[Transfers]

  implicit val currencyColumnType: JdbcType[Currency] with BaseTypedType[Currency] =
    MappedColumnType.base[Currency, String]({
      case RUB => "RUB"
      case EUR => "EUR"
      case USD => "USD"
    }, {
      case "RUB" => RUB
      case "EUR" => EUR
      case "USD" => USD
    }
  )

  private [db] class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def deleted = column[Boolean]("deleted")
    override def * = (id.?, firstName, lastName, deleted) <> (User.tupled, User.unapply)
  }

  private [db] class Rates(tag: Tag) extends Table[Rate](tag, "rates") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def sourceCurrency = column[Currency]("source_currency")
    def targetCurrency = column[Currency]("target_currency")
    def exchangeRate = column[BigDecimal]("exchange_rate", O.SqlType("decimal(10, 4)"))
    override def * = (id.?, sourceCurrency, targetCurrency, exchangeRate) <> (Rate.tupled, Rate.unapply)
  }

  private [db] class Accounts(tag: Tag) extends Table[Account](tag, "accounts") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def currency = column[Currency]("currency")
    def balance = column[BigDecimal]("balance")
    def userId = column[Long]("user_id")
    def deleted = column[Boolean]("deleted")
    override def * = (id.?, currency, balance, userId, deleted) <> (Account.tupled, Account.unapply)

    def user = foreignKey("fk__user", userId, users)(_.id)
  }

  private [db] class Transfers(tag: Tag) extends Table[Transfer](tag, "transfers") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def sourceAccount = column[Long]("source_account")
    def targetAccount = column[Long]("target_account")
    def amount = column[BigDecimal]("amount")
    def exchangeRate = column[Long]("exchange_rate_id")
    override def * = (id.?, sourceAccount, targetAccount, amount, exchangeRate) <>
      (Transfer.tupled, Transfer.unapply)

    def account = foreignKey("fk__source_account", sourceAccount, accounts)(_.id)
    def rate = foreignKey("fk__exchange_rate_id", exchangeRate, rates)(_.id,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Restrict
    )
  }

}
