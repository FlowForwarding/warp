package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from java.lang.String to the corresponding Avro type.
  */
trait AvroString extends AvroPrimitiveType[String] {
  val typeName = "string"
}

object AvroString extends AvroString