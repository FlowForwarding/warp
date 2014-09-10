package com.gensler.scalavro.io.complex.test

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.io.complex._
import com.gensler.scalavro.error._
import com.gensler.scalavro.io.AvroTypeIO

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroJEnumIOSpec extends FlatSpec with Matchers {

  val enumType = AvroType[JDirection]
  val io = enumType.io

  "AvroJEnumIO" should "be the AvroTypeIO for AvroJEnum" in {
    io.isInstanceOf[AvroJEnumIO[_]] should be (true)
  }

  it should "read and write enumerations" in {
    val out = new ByteArrayOutputStream
    io.write(JDirection.NORTH, out)
    io.write(JDirection.SOUTH, out)
    io.write(JDirection.WEST, out)
    io.write(JDirection.EAST, out)

    val in = new ByteArrayInputStream(out.toByteArray)
    io read in should equal (Success(JDirection.NORTH))
    io read in should equal (Success(JDirection.SOUTH))
    io read in should equal (Success(JDirection.WEST))
    io read in should equal (Success(JDirection.EAST))
  }

  it should "read and write enumerations as JSON" in {
    val json1 = io writeJson JDirection.NORTH
    val json2 = io writeJson JDirection.SOUTH
    val json3 = io writeJson JDirection.WEST
    val json4 = io writeJson JDirection.EAST

    io readJson json1 should equal (Success(JDirection.NORTH))
    io readJson json2 should equal (Success(JDirection.SOUTH))
    io readJson json3 should equal (Success(JDirection.WEST))
    io readJson json4 should equal (Success(JDirection.EAST))
  }

}