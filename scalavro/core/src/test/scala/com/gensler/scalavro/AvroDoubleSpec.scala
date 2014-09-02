package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroDoubleSpec extends AvroSpec {

  val ad = AvroDouble

  "AvroDouble" should "be a subclass of AvroType[Double]" in {
    ad.isInstanceOf[AvroType[Double]] should be (true)
    typeOf[ad.scalaType] =:= typeOf[Double] should be (true)
  }

  it should "be a primitive AvroType" in {
    ad.isPrimitive should be (true)
  }

}