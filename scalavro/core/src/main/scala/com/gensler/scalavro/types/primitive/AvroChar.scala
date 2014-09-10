package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Char to the corresponding Avro type.
  */
trait AvroChar extends AvroPrimitiveType[Char] {
  val typeName = "int"
}

object AvroChar extends AvroChar