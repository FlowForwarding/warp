package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroXmlSpec extends AvroSpec {

  val at = AvroType[scala.xml.Node]

  "AvroXml" should "be a subclass of AvroType[scala.xml.Node]" in {
    at should equal (AvroXml)
    at.isInstanceOf[AvroType[scala.xml.Node]] should be (true)
    typeOf[at.scalaType] =:= typeOf[scala.xml.Node] should be (true)
  }

  it should "be a primitive AvroType" in {
    at.isPrimitive should be (true)
  }

  it should "allow scala.xml.NodeSeq to be supported as a Seq[Node]" in {
    val nodeSeqType = AvroType[scala.xml.NodeSeq]
    nodeSeqType.schema should equal (AvroType[Seq[String]].schema)
  }

}