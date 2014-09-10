package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroLongSpec extends AvroSpec {

  val al = AvroLong

  "AvroLong" should "be a subclass of AvroType[Long]" in {
    al.isInstanceOf[AvroType[Long]] should be (true)
    typeOf[al.scalaType] =:= typeOf[Long] should be (true)
  }

  it should "be a primitive AvroType" in {
    al.isPrimitive should be (true)
  }

}