package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Long to the corresponding Avro type.
  */
trait AvroLong extends AvroPrimitiveType[Long] {
  val typeName = "long"
}

object AvroLong extends AvroLong