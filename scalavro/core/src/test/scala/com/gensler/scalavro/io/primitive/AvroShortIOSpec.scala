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

class AvroShortIOSpec extends FlatSpec with Matchers {

  val io = AvroShortIO

  "AvroShortIO" should "be the AvroTypeIO for AvroShort" in {
    val avroTypeIO: AvroTypeIO[_] = AvroShort.io
    avroTypeIO should be (io)
  }

  it should "read and write Shorts" in {
    val out = new ByteArrayOutputStream

    io.write(-55.toShort, out)
    io.write(65535.toShort, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(-55.toShort))
    io read in should equal (Success(65535.toShort))
  }

  it should "read and write Shorts as JSON" in {
    val json1 = io writeJson -55.toShort
    val json2 = io writeJson 65535.toShort

    io readJson json1 should equal (Success(-55.toShort))
    io readJson json2 should equal (Success(65535.toShort))
  }

}