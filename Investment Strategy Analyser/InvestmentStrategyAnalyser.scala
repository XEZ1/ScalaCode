object InvestmentStrategyAnalyser {

//two test portfolios

val blchip_portfolio = List("GOOG", "AAPL", "MSFT", "IBM", "FB", "AMZN", "BIDU")
val rstate_portfolio = List("PLD", "PSA", "AMT", "AIV", "AVB", "BXP", "CCI", 
                            "DLR", "EQIX", "EQR", "ESS", "EXR", "FRT", "HCP") 

import io.Source
import scala.util._

  def get_january_data_recursive_helper(year: Int, lstInitial: List[String], lstFinal: List[String] = Nil) : List[String] = {
    lstInitial match {
      case Nil => lstFinal
      case x::xs => {
        val checker = s"${x.lift(0).get}${x.lift(1).get}${x.lift(2).get}${x.lift(3).get}"
        if (checker == year.toString) get_january_data_recursive_helper(year, xs, lstFinal :+ x) else get_january_data_recursive_helper(year, xs, lstFinal)
      }
    }
  }

  def get_january_data(symbol: String, year: Int) : List[String] = {
    val readResource = io.Source.fromFile(s"${symbol}.csv").getLines().toList
    val toReturn: List[String] = readResource match {
      case x::xs => get_january_data_recursive_helper(year, xs)
    }
    toReturn
  }

  def get_first_price_recursive_helper(lstInitial: List[String], lstFinal: List[(Double, String)] = Nil) : List[(Double, String)]  = {
    val toReturn: List[(Double, String)]  = lstInitial match {
      case Nil => lstFinal
      case x::xs => get_first_price_recursive_helper(xs, lstFinal :+ (x.toString().split(",").toList.head.split("-").toList.lift(2).get.toDouble, x))
    }
    toReturn
  }

  def get_first_price(symbol: String, year: Int) : Option[Double] = {
    val data: List[String] = get_january_data(symbol, year)
    val sortedData: List[(Double, String)] = get_first_price_recursive_helper(data)
    if (sortedData.nonEmpty) Some(sortedData.toSeq.sortBy(_._1).head._2.split(",").toList.takeRight(1).head.toDouble) else None
  }

  def get_prices_recursive_helper(portfolio: List[String], year: Int, lstFinal: List[Option[Double]] = Nil) : List[Option[Double]] = {
    val toReturn: List[Option[Double]] = portfolio match {
      case Nil => lstFinal
      case x::xs => get_prices_recursive_helper(xs, year, lstFinal :+ (get_first_price(x, year)))
    }
    toReturn
  }

  def get_prices_1(portfolio: List[String], years: Range, lstFinal: List[List[Option[Double]]] = Nil) : List[List[Option[Double]]] = {
    val toReturn: List[List[Option[Double]]] = years.toList match {
      case Nil => lstFinal
      case x::xs if(xs.nonEmpty) => get_prices_1(portfolio, xs.head to (xs.takeRight(1).head), lstFinal :+ get_prices_recursive_helper(portfolio, x))
      case _ => get_prices_1(portfolio, 0 until 0, lstFinal :+ get_prices_recursive_helper(portfolio, years.toList.head))
    }
    toReturn
  }

  def get_prices(portfolio: List[String], years: Range) : List[List[Option[Double]]] = {
    get_prices_1(portfolio, years)
  }

  def get_delta(price_old: Option[Double], price_new: Option[Double]) : Option[Double] = {
    val price_old_unwrapped = price_old.getOrElse(None)
    val price_new_unwrapped = price_new.getOrElse(None)
    if (price_old_unwrapped != None && price_new_unwrapped != None) Some((price_new.get - price_old.get) / price_old.get) else None
  }

  def get_deltas_recursive_helper(data: List[List[Option[Double]]], lstFinal: List[List[Option[Double]]] = Nil) : List[List[Option[Double]]] = {
    val toReturn: List[List[Option[Double]]] = data match {
      case Nil => lstFinal
      case x::xs if(xs.nonEmpty) => get_deltas_recursive_helper(xs, lstFinal :+ (for ((elementOld, elementNew) <- x.zip(xs.head)) yield get_delta(elementOld, elementNew)))
      case _ => lstFinal
    }
    toReturn
  }

  def get_deltas(data: List[List[Option[Double]]]) : List[List[Option[Double]]] = {
    get_deltas_recursive_helper(data)
  }

  def yearly_yield(data: List[List[Option[Double]]], balance: Long, index: Int) : Long = {
    val sortedData = data(index).filter(value => value.isDefined)
    val profitFromOneStock = for (changeFactor <- sortedData) yield {
      changeFactor.get * (balance/sortedData.length)
    }
    balance + profitFromOneStock.sum.toLong
  }

  def compound_yield(data: List[List[Option[Double]]], balance: Long, index: Int) : Long = {
    if (index != data.length) {
      val sortedData = data(index).filter(value => value.isDefined)
      val profitFromOneStock = for (changeFactor <- sortedData) yield {
        changeFactor.get * (balance/sortedData.length)
      }
      compound_yield(data, balance + profitFromOneStock.sum.toLong, index + 1)
    }
    else {
      balance
    }
  }

  def investment(portfolio: List[String], years: Range, start_balance: Long) : Long = {
    compound_yield(get_deltas(get_prices(portfolio, years)), start_balance, 0)
  }




//Test cases for the two portfolios given above

//println("Real data: " + investment(rstate_portfolio, 1978 to 2019, 100))
//println("Blue data: " + investment(blchip_portfolio, 1978 to 2019, 100))

}

