package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroFloatSpec extends AvroSpec {

  val af = AvroFloat

  "AvroFloat" should "be a subclass of AvroType[Float]" in {
    af.isInstanceOf[AvroType[Float]] should be (true)
    typeOf[af.scalaType] =:= typeOf[Float] should be (true)
  }

  it should "be a primitive AvroType" in {
    af.isPrimitive should be (true)
  }

}