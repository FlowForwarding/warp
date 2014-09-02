package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Short to the corresponding Avro type.
  */
trait AvroShort extends AvroPrimitiveType[Short] {
  val typeName = "int"
}

object AvroShort extends AvroShort