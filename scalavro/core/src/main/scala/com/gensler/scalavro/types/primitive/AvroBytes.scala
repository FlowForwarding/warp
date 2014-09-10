package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from Seq[Byte] to the corresponding Avro type.
  */
trait AvroBytes extends AvroPrimitiveType[Seq[Byte]] {
  val typeName = "bytes"
}

object AvroBytes extends AvroBytes