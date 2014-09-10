package com.gensler.scalavro.test

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.{ AvroRecord, AvroUnion }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

class ReferenceSpec extends AvroSpec {

  "AvroRecord" should "return a referenced union schema" in {
    val Success(listType: AvroRecord[_]) = AvroType.fromType[SinglyLinkedStringList]
    // prettyPrint(listType.schema)
  }

}