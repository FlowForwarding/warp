package com.gensler.scalavro.io.complex.test

import scala.util.{ Try, Success, Failure }

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.io.complex._

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }
import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.io.complex.AvroFixedSizeEnumIO
import scala.util.Success

object FixedSizeDirection extends DWordEnum {
  type FixedSizeDirection = Value
  val NORTH = value(1)
  val EAST  = value(2)
  val SOUTH = value(3)
  val WEST  = value(1)
}

object FixedSizeDirectionWithUnspecifiedValues extends WordEnum with AllowUnspecifiedValues[Short]{
  type FixedSizeDirectionWithUnspecifiedValues = Value
  val U_NORTH = value(1)
  val U_EAST  = value(2)
  val U_SOUTH = value(3)
  val U_WEST  = value(1)
}

object FixedSizeDirectionBitmap extends WordBitmap{
  type FixedSizeDirectionBitmap = Value
  val B_NORTH = value(1)
  val B_EAST  = value(2)
  val B_SOUTH = value(4)
  val B_WEST  = value(8)
}

class AvroFixedSizeEnumIOSpec extends FlatSpec with Matchers {
  import FixedSizeDirection._
  import FixedSizeDirectionWithUnspecifiedValues._
  import FixedSizeDirectionBitmap._

  val enumType = AvroType[FixedSizeDirection]
  val enumTypeU = AvroType[FixedSizeDirectionWithUnspecifiedValues]
  val enumTypeB = AvroType[FixedSizeDirectionBitmap]
  val io = enumType.io
  val ioU = enumTypeU.io
  val ioB = enumTypeB.io

  "AvroFixedSizeEnumIO" should "be the AvroTypeIO for AvroFixedSizeEnum" in {
    io.isInstanceOf[AvroFixedSizeEnumIO[_, _, _]] should be (true)
  }

  it should "read and write enumerations" in {
    val out = new ByteArrayOutputStream
    io.write(NORTH, out)
    io.write(SOUTH, out)
    io.write(WEST, out)
    io.write(EAST, out)

    val in = new ByteArrayInputStream(out.toByteArray)

    io read in should equal (Success(NORTH))
    io read in should equal (Success(SOUTH))
    io read in should equal (Success(WEST))
    io read in should equal (Success(EAST))
  }

  it should "notify about unresolved values but still be able to read them" in {
    val out = new ByteArrayOutputStream
    io.write(NORTH, out)
    io.write(WEST, out)

    val in = new ByteArrayInputStream(out.toByteArray)

    val north = io read in
    north should equal (Success(NORTH))
    north.get.toString should equal ("Unresolved(1)")

    val west = io read in
    west should equal (Success(WEST))
    west.get.toString should equal ("Unresolved(1)")
  }

  it should "be able to handle unspecified value if it is required" in {
    val data: Short = 1234
    val container = FixedSizeDirectionWithUnspecifiedValues.Unspecified(data)

    val out = new ByteArrayOutputStream
    ioU.write(container, out)

    val in = new ByteArrayInputStream(out.toByteArray)

    val value = ioU read in
    value should equal (Success(container))
    value.get.toString should equal ("Unspecified(1234)")
  }

  it should "be able to handle set of values if it is required" in {
    val container = FixedSizeDirectionBitmap.ValuesSet(Set(B_EAST, B_NORTH, B_WEST))

    val out = new ByteArrayOutputStream
    ioB.write(container, out)

    val in = new ByteArrayInputStream(out.toByteArray)

    val north = ioB read in
    north should equal (Success(container))
    north.get.toString should equal ("Set(B_NORTH, B_EAST, B_WEST)")
  }

  it should "read and write enumerations as JSON" in {
    val json1 = io writeJson NORTH
    val json2 = io writeJson SOUTH
    val json3 = io writeJson WEST
    val json4 = io writeJson EAST

    io readJson json1 should equal (Success(NORTH))
    io readJson json2 should equal (Success(SOUTH))
    io readJson json3 should equal (Success(WEST))
    io readJson json4 should equal (Success(EAST))
  }

}