package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroBytesSpec extends AvroSpec {

  lazy val ab = AvroType.fromType[Seq[Byte]].get.asInstanceOf[AvroBytes]

  "AvroBytes" should "be a subclass of AvroType[Seq[Byte]]" in {
    ab == AvroBytes should be (true)
    ab.isInstanceOf[AvroBytes] should be (true)
    typeOf[ab.scalaType] =:= typeOf[Seq[Byte]] should be (true)
  }

  it should "be a primitive AvroType" in {
    ab.isPrimitive should be (true)
  }

}