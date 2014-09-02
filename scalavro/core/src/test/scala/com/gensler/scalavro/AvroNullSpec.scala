package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroNullSpec extends AvroSpec {

  val aType = AvroNull

  "AvroNull" should "be a subclass of AvroType[Unit]" in {
    aType.isInstanceOf[AvroType[Unit]] should be (true)
    typeOf[aType.scalaType] =:= typeOf[Unit] should be (true)
  }

  it should "be a primitive AvroType" in {
    aType.isPrimitive should be (true)
  }

}