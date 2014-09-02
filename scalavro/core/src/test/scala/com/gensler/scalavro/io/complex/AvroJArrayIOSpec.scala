package com.gensler.scalavro.io.complex.test

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.io.complex._
import com.gensler.scalavro.error._

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroJArrayIOSpec extends FlatSpec with Matchers {

  val intArrayType = AvroType[Array[Int]]
  val io = intArrayType.io

  "AvroJArrayIO" should "be the AvroTypeIO for AvroJArray" in {
    io.isInstanceOf[AvroJArrayIO[_]] should be (true)
  }

  it should "read and write arrays" in {
    val a = (0 to 1000).toArray

    val out = new ByteArrayOutputStream
    io.write(a, out)

    val in = new ByteArrayInputStream(out.toByteArray)
    io.read(in).map { _.toSeq } should equal (Success(a.toSeq))
  }

  it should "read and write arrays as JSON" in {
    val a = (0 to 1000).toArray
    val json = io writeJson a
    io.readJson(json).map { _.toSeq } should equal (Success(a.toSeq))
  }

}