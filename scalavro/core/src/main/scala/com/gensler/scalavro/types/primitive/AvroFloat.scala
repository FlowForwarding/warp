package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Float to the corresponding Avro type.
  */
trait AvroFloat extends AvroPrimitiveType[Float] {
  val typeName = "float"
}

object AvroFloat extends AvroFloat