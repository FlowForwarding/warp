package com.gensler.scalavro.types

import com.gensler.scalavro.JsonSchemaProtocol._

import scala.collection.immutable.ListMap
import scala.reflect.runtime.universe.TypeTag

import spray.json._

/**
  * Parent class of all "named types".  As of version 1.7.5 of the Avro
  * specification, the named types are `Record`, `Enum`, and `Fixed`.
  */
abstract class AvroNamedType[T: TypeTag] extends AvroComplexType[T] {

  def name(): String

  def namespace(): Option[String]

  final def fullyQualifiedName(): String =
    namespace.map { "%s.%s".format(_, name) } getOrElse name

  final override def canonicalFormOrFullyQualifiedName = this.fullyQualifiedName.toJson

}
