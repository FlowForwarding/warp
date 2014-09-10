package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

/**
  * Represents a mapping from scala.xml.Node to the corresponding Avro type.
  */
trait AvroXml extends AvroPrimitiveType[scala.xml.Node] {
  val typeName = "string"
}

object AvroXml extends AvroXml