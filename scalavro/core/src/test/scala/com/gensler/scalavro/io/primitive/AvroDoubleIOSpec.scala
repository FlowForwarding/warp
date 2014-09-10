package com.gensler.scalavro.io.primitive.test

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.io.primitive._
import com.gensler.scalavro.error._

import com.gensler.scalavro.io.AvroTypeIO

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroDoubleIOSpec extends FlatSpec with Matchers {

  val io = AvroDoubleIO

  "AvroDoubleIO" should "be the AvroTypeIO for AvroDouble" in {
    val avroTypeIO: AvroTypeIO[_] = AvroDouble.io
    avroTypeIO should be (io)
  }

  it should "read and write Doubles" in {
    val out = new ByteArrayOutputStream

    io.write(math.Pi, out)
    io.write(1.23, out)
    io.write(-1500.123, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(math.Pi))
    io read in should equal (Success(1.23))
    io read in should equal (Success(-1500.123))
  }

  it should "read and write Doubles as JSON" in {
    val piJson = io writeJson math.Pi
    val smallJson = io writeJson 1.23
    val bigNegJson = io writeJson -1500.123
    val biggestJson = io writeJson Double.MaxValue
    val smallestJson = io writeJson Double.MinValue

    io readJson piJson should equal (Success(math.Pi))
    io readJson smallJson should equal (Success(1.23))
    io readJson bigNegJson should equal (Success(-1500.123))
    io readJson biggestJson should equal (Success(Double.MaxValue))
    io readJson smallestJson should equal (Success(Double.MinValue))
  }

}