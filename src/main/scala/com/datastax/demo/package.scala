package com.datastax

import java.text.SimpleDateFormat
import java.util.Date

import scala.util.Random

/**
 * Created by jameskavanagh on 09/02/2015.
 */
package object demo {

  case class PushTxn()
  case class Txn(card_no : String, merchant : String, amount : Double, txn_time : Date)
  case class Start()
  case class Stop()
  case class StartDispatch()
  case class DispatchedLocally()
  case class Dispatched(num : Int)
  case class OutputInitialised()

  object Txn {

    def txnDateFormat = new SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ssZ")

    def apply(array : Array[String]) : Txn = {
      Txn(card_no = array(0), merchant = array(1), amount = array(2).toDouble, txn_time = txnDateFormat.parse(array(3)))
    }

    implicit def marshallTxn(txn : Txn) : String = {
      val dateTime = txnDateFormat.format(txn.txn_time)
      f"${txn.card_no}%s,${txn.merchant}%s,${txn.amount}%.2f,${dateTime}%s"
    }

  }

  val MERCHANTS = Array("Starbucks","Costa","Amazon","National Rail", "British Airways", "KLM",
    "Boots", "Pizza Express", "Tesco", "Esso","Co-Op","Sainsburys","Swift Taxis","TJ Landscapes",
    "Petcare Vets", "Burger King", "Hilton Hotels","British Telecomm","EE","Vodafone","Smiley Dentists",
    "BUPA Healthcare", "Hertfordshire County Council", "Topshop", "Primark")

  val CREDIT_CARDS : Array[String] = generateCards()

  val TOPIC_NAME : String = "CC_TXNS"

  private[demo] def generateCards(numOfCards : Int = 20) : Array[String] = {
    def generateCard : String = {
      var prefix = "4"
      (0 to 15).foldLeft(prefix) { (x, num) =>
        x + Random.nextInt(9)
      }
    }

    (0 to numOfCards-1).foldLeft(List.empty[String]) { (res : List[String], num) =>
      generateCard :: res
    }.toArray
  }

}
