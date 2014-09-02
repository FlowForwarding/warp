package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType

import scala.reflect.runtime.universe._

class AvroError[T: TypeTag](
  name: String,
  fields: Seq[AvroRecord.Field[_]],
  aliases: Seq[String] = Seq(),
  namespace: Option[String] = None,
  doc: Option[String] = None) extends AvroRecord[T](
  name,
  fields,
  aliases,
  namespace,
  doc
) {
  override val typeName = "error"
}
