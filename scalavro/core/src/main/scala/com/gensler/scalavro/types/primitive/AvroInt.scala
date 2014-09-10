package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Int to the corresponding Avro type.
  */
trait AvroInt extends AvroPrimitiveType[Int] {
  val typeName = "int"
}

object AvroInt extends AvroInt