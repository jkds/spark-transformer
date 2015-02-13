package com.datastax.demo

import java.util.Date

import org.scalatest.FlatSpec

/**
 * Created by jameskavanagh on 09/02/2015.
 */
class PackageTest extends FlatSpec {

  import Txn._

  "The implicit converter" should "convert correctly" in {
    val txn = Txn("454556565656","Blah",10002.3432,new Date())
    val x = marshallTxn(txn)
    println(x)

  }





}
