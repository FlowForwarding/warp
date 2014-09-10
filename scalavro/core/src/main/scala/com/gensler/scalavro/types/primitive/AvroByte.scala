package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.Byte to the corresponding Avro type.
  */
trait AvroByte extends AvroPrimitiveType[Byte] {
  val typeName = "int"
}

object AvroByte extends AvroByte