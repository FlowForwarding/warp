package com.gensler.scalavro.io.primitive.test

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.{Matchers, FlatSpec}

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.io.primitive._
import com.gensler.scalavro.error._

import com.gensler.scalavro.io.AvroTypeIO

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroIntIOSpec extends FlatSpec with Matchers {

  val io = AvroIntIO

  "AvroIntIO" should "be the AvroTypeIO for AvroInt" in {
    val avroTypeIO: AvroTypeIO[_] = AvroInt.io
    avroTypeIO should be (io)
  }

  it should "read and write Ints" in {
    val out = new ByteArrayOutputStream

    io.write(55, out)
    io.write(8675309, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(55))
    io read in should equal (Success(8675309))
  }

  it should "read and write Ints as JSON" in {
    val json1 = io writeJson -55
    val json2 = io writeJson 8675309

    io readJson json1 should equal (Success(-55))
    io readJson json2 should equal (Success(8675309))
  }

}