package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Double to the corresponding Avro type.
  */
trait AvroDouble extends AvroPrimitiveType[Double] {
  val typeName = "double"
}

object AvroDouble extends AvroDouble