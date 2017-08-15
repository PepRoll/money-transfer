package me.peproll.moneytransfer.mocks

import me.peproll.moneytransfer.model.Rate
import me.peproll.moneytransfer.services.IRateService

import scala.concurrent.ExecutionContext

class RateServiceMock(rates: Seq[Rate])(implicit executionContext: ExecutionContext)
  extends CommonServiceMock[Rate](rates)
  with IRateService
