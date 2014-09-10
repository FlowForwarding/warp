package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroBooleanSpec extends AvroSpec {

  val ab = AvroBoolean

  "AvroBoolean" should "be a subclass of AvroType[Boolean]" in {
    ab.isInstanceOf[AvroType[Boolean]] should be (true)
    typeOf[ab.scalaType] =:= typeOf[Boolean] should be (true)
  }

  it should "be a primitive AvroType" in {
    ab.isPrimitive should be (true)
  }

}