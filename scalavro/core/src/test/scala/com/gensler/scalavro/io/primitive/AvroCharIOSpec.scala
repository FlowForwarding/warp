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

class AvroCharIOSpec extends FlatSpec with Matchers {

  val io = AvroCharIO

  "AvroCharIO" should "be the AvroTypeIO for AvroChar" in {
    val avroTypeIO: AvroTypeIO[_] = AvroChar.io
    avroTypeIO should be (io)
  }

  it should "read and write Chars" in {
    val out = new ByteArrayOutputStream

    io.write('A', out)
    io.write('%', out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success('A'))
    io read in should equal (Success('%'))
  }

  it should "read and write Chars as JSON" in {
    val aJson = io writeJson 'A'
    val percentJson = io writeJson '%'

    io readJson aJson should equal (Success('A'))
    io readJson percentJson should equal (Success('%'))
  }

}