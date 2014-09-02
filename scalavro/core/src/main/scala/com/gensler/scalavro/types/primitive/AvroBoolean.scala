package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Boolean to the corresponding Avro type.
  */
trait AvroBoolean extends AvroPrimitiveType[Boolean] {
  val typeName = "boolean"
}

object AvroBoolean extends AvroBoolean