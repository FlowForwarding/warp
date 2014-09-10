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

class AvroBytesIOSpec extends FlatSpec with Matchers {

  val io = AvroBytesIO

  "AvroBytesIO" should "be the AvroTypeIO for AvroBytes" in {
    val avroTypeIO: AvroTypeIO[_] = AvroBytes.io
    avroTypeIO should be (io)
  }

  it should "read and write bytes" in {
    val text = "The quick brown fox jumped over the lazy dog."
    val out = new ByteArrayOutputStream
    io.write(text.getBytes.toSeq, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)
    io.read(in).get.toSeq should equal (text.getBytes.toSeq)
  }

  it should "read and write bytes as JSON" in {
    val text = "The quick brown fox jumped over the lazy dog."
    val json = io writeJson text.getBytes.toSeq
    (io readJson json).get should equal (text.getBytes.toSeq)
  }

}