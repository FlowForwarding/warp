package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroStringSpec extends AvroSpec {

  val as = AvroString

  "AvroString" should "be a subclass of AvroType[String]" in {
    as.isInstanceOf[AvroType[String]] should be (true)
    typeOf[as.scalaType] =:= typeOf[String] should be (true)
  }

  it should "be a primitive AvroType" in {
    as.isPrimitive should be (true)
  }

}