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

class AvroNullIOSpec extends FlatSpec with Matchers {

  val io = AvroNullIO

  "AvroNullIO" should "be the AvroTypeIO for AvroNull" in {
    val avroTypeIO: AvroTypeIO[_] = AvroNull.io
    avroTypeIO should be (io)
  }

  it should "read and write Units" in {
    val out = new ByteArrayOutputStream

    io.write((), out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(()))
  }

  it should "read and write Units as JSON" in {
    val json = io.writeJson(())
    io readJson json should equal (Success(()))
  }

}