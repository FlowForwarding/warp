package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Unit to the corresponding Avro type.
  */
trait AvroNull extends AvroPrimitiveType[Unit] {
  val typeName = "null"
}

object AvroNull extends AvroNull