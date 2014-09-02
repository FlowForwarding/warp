package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex.AvroRecord
import com.gensler.scalavro.error._

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroRecordSpec extends AvroSpec {

  val personType = AvroType[Person]
  val santaListType = AvroType[SantaList]

  "AvroRecord" should "be parameterized with its corresponding Scala type" in {
    personType.isInstanceOf[AvroType[Person]] should be (true)
    typeOf[personType.scalaType] =:= typeOf[Person] should be (true)
  }

  it should "be a complex AvroType" in {
    personType.isPrimitive should be (false)
    santaListType.isPrimitive should be (false)
  }

  it should "output a fully self-contained schema" in {

    santaListType match {
      case recordType: AvroRecord[_] => {
        // println("santaListType.selfContainedSchema:")
        // println(recordType.selfContainedSchema())
      }
    }

  }

}