package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroIntSpec extends AvroSpec {

  val ai = AvroInt

  "AvroInt" should "be a subclass of AvroType[Int]" in {
    ai.isInstanceOf[AvroType[Int]] should be (true)
    typeOf[ai.scalaType] =:= typeOf[Int] should be (true)
  }

  it should "be a primitive AvroType" in {
    ai.isPrimitive should be (true)
  }

}