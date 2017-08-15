package me.peproll.moneytransfer.model

sealed trait Currency
case object RUB extends Currency
case object EUR extends Currency
case object USD extends Currency