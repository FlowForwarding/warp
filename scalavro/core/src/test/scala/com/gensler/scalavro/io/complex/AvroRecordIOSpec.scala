package com.gensler.scalavro.io.complex.test

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.io.complex._
import com.gensler.scalavro.error._

import com.gensler.scalavro.io.AvroTypeIO

import java.io.{
  PipedInputStream,
  PipedOutputStream
}
import com.gensler.scalavro.types.supply.{RawSeqFieldsInfo, RawSeq}

// for testing
case class Person(name: String, age: Int)

// for testing
case class SantaList(nice: Seq[Person], naughty: Seq[Person])

case class SimpleRawSeq(len1: Int, seq1: RawSeq[Int], len2: Int, seq2: RawSeq[Int])

object SimpleRawSeq extends RawSeqFieldsInfo {
  val rawFieldsLengthCalculator: LengthCalculator = {
    case 1 => lenByIndex(0)
    case 3 => lenByIndex(2)
  }
}

class AvroRecordIOSpec extends FlatSpec with Matchers {

  "AvroRecordIO" should "be the AvroTypeIO for AvroRecord" in {
    val personIO = AvroType[Person].io
    val avroTypeIO: AvroTypeIO[_] = personIO
    avroTypeIO should equal (personIO)
  }

  it should "read and write simple records" in {
    val out = new PipedOutputStream
    val in = new PipedInputStream(out)

    val personIO = AvroType[Person].io

    val julius = Person("Julius Caesar", 2112)

    personIO.write(julius, out)
    personIO read in should equal (Success(julius))
  }

  it should "read and write complex record instances" in {
    val out = new PipedOutputStream
    val in = new PipedInputStream(out)

    val santaListIO = AvroType[SantaList].io

    val sList = SantaList(
      nice = Seq(Person("Suzie", 9)),
      naughty = Seq(Person("Tommy", 7), Person("Eve", 3))
    )

    santaListIO.write(sList, out)
    santaListIO read in should equal (Success(sList))
  }

  it should "read and write HandshakeRequest instances" in {
    import com.gensler.scalavro.protocol.{ HandshakeRequest, MD5 }

    val out = new PipedOutputStream
    val in = new PipedInputStream(out)

    val handshakeRequestIO = AvroType[HandshakeRequest].io

    val request = HandshakeRequest(
      clientHash = MD5("abcd1234defg5678".getBytes.toIndexedSeq),
      clientProtocol = Some("{}"), // None,
      serverHash = MD5("abcd1234defg5678".getBytes.toIndexedSeq),
      meta = Some(Map[String, Seq[Byte]]()) // None
    )

    handshakeRequestIO.write(request, out)
    val readResult = (handshakeRequestIO read in).get
    readResult should equal (request)
  }

  it should "read and write an object graph with shared references" in {
    val out = new PipedOutputStream
    val in = new PipedInputStream(out)

    val santaListIO = AvroType[SantaList].io

    val suzie = Person("Suzie", 9)

    val sList = SantaList(
      nice = Seq(suzie, Person("Dennis", 4)),
      naughty = Seq(Person("Tommy", 7), suzie, Person("Eve", 3))
    )

    santaListIO.write(sList, out)
    santaListIO read in should equal (Success(sList))
  }

  it should "read and write instances of recursively defined case classes" in {
    val out = new PipedOutputStream
    val in = new PipedInputStream(out)

    import com.gensler.scalavro.test.{ SinglyLinkedStringList => LL }

    val listIO = AvroType[LL].io

    val myList = LL(
      "one",
      Some(LL(
        "two",
        Some(LL(
          "three",
          None
        ))
      ))
    )

    listIO.write(myList, out)
    val Success(readResult) = listIO read in
    readResult should equal (myList)
  }

  it should "read and write simple records as JSON" in {
    val personIO = AvroType[Person].io

    val julius = Person("Julius Caesar", 2112)
    val json = personIO writeJson julius
    personIO readJson json should equal (Success(julius))
  }

  it should "read and write complex record instances as JSON" in {
    val santaListIO = AvroType[SantaList].io

    val sList = SantaList(
      nice = Seq(Person("Suzie", 9)),
      naughty = Seq(Person("Tommy", 7), Person("Eve", 3))
    )

    val json = santaListIO writeJson sList
    santaListIO readJson json should equal (Success(sList))
  }

  it should "read and write record instances with fields of type RawSeq" in {
    val out = new PipedOutputStream
    val in = new PipedInputStream(out)

    val personIO = AvroType[SimpleRawSeq].io

    val seq = SimpleRawSeq(3, RawSeq(1, 1, 1), 4, RawSeq(2, 2, 2, 2))

    personIO.write(seq, out)
    personIO read in should equal (Success(seq))
  }
}