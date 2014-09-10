package com.gensler.scalavro.io.complex.test

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.io.complex._
import com.gensler.scalavro.types.supply.RawSeq
import com.gensler.scalavro.types.supply.RawSeq._

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroRawArrayIOSpec extends FlatSpec with Matchers {

  val intRawArrayType = AvroType[RawSeq[Int]]
  val io = intRawArrayType.io

  "AvroRawArrayIO" should "be the AvroTypeIO for AvroRawArray" in {
    io.isInstanceOf[AvroRawArrayIO[_, _]] should be (true)
  }

  it should "read and write arrays" in {
    val a = RawSeq(0 to 10: _*)

    val out = new ByteArrayOutputStream
    io.write(a, out)

    val in = new ByteArrayInputStream(out.toByteArray)
    io.read(in).map { _.toSeq } should equal (Success(a.toSeq))
  }

  it should "read and write arrays as JSON" in {
    val a = RawSeq(0 to 10: _*)
    val json = io writeJson a
    io.readJson(json).map { _.toSeq } should equal (Success(a.toSeq))
  }

}