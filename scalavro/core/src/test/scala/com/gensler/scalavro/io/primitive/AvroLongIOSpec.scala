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

class AvroLongIOSpec extends FlatSpec with Matchers {

  val io = AvroLongIO

  "AvroLongIO" should "be the AvroTypeIO for AvroLong" in {
    val avroTypeIO: AvroTypeIO[_] = AvroLong.io
    avroTypeIO should be (io)
  }

  it should "read and write Longs" in {
    val out = new ByteArrayOutputStream

    io.write(-55L, out)
    io.write(8675309L, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(-55L))
    io read in should equal (Success(8675309L))
  }

  it should "read and write Ints as JSON" in {
    val json1 = io writeJson -55L
    val json2 = io writeJson 8675309L

    io readJson json1 should equal (Success(-55L))
    io readJson json2 should equal (Success(8675309L))
  }

}