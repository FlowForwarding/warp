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

class AvroXmlIOSpec extends FlatSpec with Matchers {

  val io = AvroXmlIO

  "AvroXmlIO" should "be the AvroTypeIO for AvroXml" in {
    val avroTypeIO: AvroTypeIO[_] = AvroXml.io
    avroTypeIO should equal (io)
  }

  it should "read and write XML" in {
    val xml = <node>hello from XML!</node>

    val out = new ByteArrayOutputStream
    io.write(xml, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io.read(in).get should equal (xml)
  }

  it should "read and write XML as JSON" in {
    val xml = <node>hello from XML!</node>

    val json = io writeJson xml
    io readJson json should equal (Success(xml))
  }

}