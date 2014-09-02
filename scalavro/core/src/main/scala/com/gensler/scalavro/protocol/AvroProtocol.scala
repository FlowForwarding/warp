package com.gensler.scalavro.protocol

import com.gensler.scalavro.Reference
import com.gensler.scalavro.types.{ AvroType, AvroNamedType }
import com.gensler.scalavro.types.complex.{ AvroRecord, AvroUnion }
import com.gensler.scalavro.{ CanonicalForm, JsonSchemifiable }
import com.gensler.scalavro.JsonSchemaProtocol._
import com.gensler.scalavro.types.SelfDescribingSchemaHelpers._

import scala.language.existentials
import scala.collection.immutable.ListMap
import scala.collection.mutable

import java.security.MessageDigest

import spray.json._

/**
  * Avro protocols describe RPC interfaces. Like schemas, they are defined with
  * JSON text.
  *
  * A protocol is a JSON object with the following attributes:
  *
  * protocol, a string, the name of the protocol (required);
  *
  * namespace, an optional string that qualifies the name;
  *
  * doc, an optional string describing this protocol;
  *
  * types, an optional list of definitions of named types (records, enums,
  * fixed and errors). An error definition is just like a record definition
  * except it uses "error" instead of "record". Note that forward references
  * to named types are not permitted.
  *
  * messages, an optional JSON object whose keys are message names and whose
  * values are objects whose attributes are described below. No two messages
  * may have the same name.
  *
  * The name and namespace qualification rules defined for schema objects apply
  * to protocols as well.
  */
case class AvroProtocol(
    protocol: String,
    types: Seq[AvroNamedType[_]],
    messages: Map[String, AvroProtocol.Message],
    namespace: Option[String] = None,
    doc: Option[String] = None) extends JsonSchemifiable with CanonicalForm {

  import scala.util.Sorting, scala.math.Ordering

  private[scalavro] implicit object DependencyOrdering extends Ordering[AvroNamedType[_]] {
    def compare(t1: AvroNamedType[_], t2: AvroNamedType[_]) = {
      ((t1 dependsOn t2), (t2 dependsOn t1)) match {
        case (false, false) => 0 // no dependencies, order doesn't matter
        case (true, false)  => 1 // t1 needs to be declared before t2
        case (false, true)  => -1 // t2 needs to be declared before t1
        case (true, true) => throw new IllegalArgumentException(
          "Detected a symmetric dependency between types [%s] and [%s].".format(t1, t2)
        )
      }
    }
  }

  lazy val normalizedDeclarations: Seq[AvroNamedType[_]] =
    AvroType[Reference].asInstanceOf[AvroNamedType[_]] +: Sorting.stableSort(types)

  def schema(): JsValue = {
    val requiredParams = ListMap(
      "protocol" -> protocol.toJson,
      "types" -> normalizedDeclarations.asInstanceOf[Seq[JsonSchemifiable]].toJson,
      "messages" -> messages.asInstanceOf[Map[String, JsonSchemifiable]].toJson
    )

    val optionalParams = Map(
      "namespace" -> namespace,
      "doc" -> doc
    ) collect { case (k, Some(v)) => (k, v.toJson) }

    (requiredParams ++ optionalParams).toJson
  }

  /**
    * Returns the JSON schema for this protocol in "parsing canonical form".
    */
  def parsingCanonicalForm(): JsValue = {
    val fullyQualifiedName = namespace.map { "%s.%s" format (_, protocol) } getOrElse protocol

    ListMap(
      "protocol" -> fullyQualifiedName.toJson,
      "types" -> normalizedDeclarations.view.zipWithIndex.map {
        case (protocolType, index) =>
          val previousTypeNames = mutable.Set[String](normalizedDeclarations.view(0, index).map(_.fullyQualifiedName): _*)
          schemaToParsingCanonicalForm(protocolType.selfContainedSchema(previousTypeNames))
      }.toSeq.toJson,
      "messages" -> messages.asInstanceOf[Map[String, CanonicalForm]].toJson
    ).toJson
  }

  /**
    * Returns the result of computing MD5 over this protocol's parsing
    * canonical form.
    */
  final lazy val fingerprint: Array[Byte] = {
    val MD5 = MessageDigest.getInstance("MD5")
    MD5.digest(parsingCanonicalForm.toString.getBytes)
  }

}

object AvroProtocol {
  /**
    * A message has attributes:
    *
    * a doc, an optional description of the message,
    *
    * a request, a list of named, typed parameter schemas (this has the same form
    * as the fields of a record declaration);
    *
    * a response schema;
    *
    * an optional union of declared error schemas. The effective union has
    * "string" prepended to the declared union, to permit transmission of
    * undeclared "system" errors. For example, if the declared error union is
    * ["AccessError"], then the effective union is ["string", "AccessError"].
    * When no errors are declared, the effective error union is ["string"].
    * Errors are serialized using the effective union; however, a protocol's
    * JSON declaration contains only the declared union.
    *
    * an optional one-way boolean parameter.
    *
    * A request parameter list is processed equivalently to an anonymous record.
    * Since record field lists may vary between reader and writer, request
    * parameters may also differ between the caller and responder, and such
    * differences are resolved in the same manner as record field differences.
    *
    * The one-way parameter may only be true when the response type is "null" and
    * no errors are listed.
    */
  case class Message(
      request: AvroRecord[_],
      response: AvroType[_],
      errors: Option[AvroUnion[_, _]] = None,
      doc: Option[String] = None,
      oneWay: Option[Boolean] = None) extends JsonSchemifiable with CanonicalForm {

    def requestParameters: Map[String, AvroType[_]] = request.fields.map {
      field => field.name -> field.fieldType
    }.toMap

    def schema(): JsValue = {
      val docParam = Map("doc" -> doc) collect { case (k, Some(v)) => (k, v.toJson) }
      new JsObject(parsingCanonicalForm.asJsObject.fields ++ docParam)
    }

    protected[this] def typeNames: mutable.Set[String] = {
      val types = {
        Seq(AvroType[Reference].asInstanceOf[AvroNamedType[_]]) ++
          request.dependentNamedTypes ++
          response.dependentNamedTypes
      }

      mutable.Set[String](types.map(_.fullyQualifiedName): _*)
    }

    /**
      * Returns the JSON schema for this message in "parsing canonical form".
      */
    def parsingCanonicalForm(): JsValue = {
      val requiredParams = Map(

        "request" -> Seq(
          new JsObject(ListMap(
            "type" -> schemaToParsingCanonicalForm(request.selfContainedSchema(typeNames)),
            "name" -> "request".toJson
          ))
        ).toJson,

        "response" -> schemaToParsingCanonicalForm(response.selfContainedSchema(typeNames))
      )

      val errorParam = Map("errors" -> errors) collect {
        case (k, Some(errorUnion)) => {
          val errorUnionTypes = AvroType[Reference].asInstanceOf[AvroNamedType[_]] +: errorUnion.dependentNamedTypes
          val errorUnionTypeNames = mutable.Set(errorUnionTypes.map(_.fullyQualifiedName): _*)
          val errorUnionSchema = errorUnion.selfContainedSchema(errorUnionTypeNames)
          k -> schemaToParsingCanonicalForm(errorUnionSchema)
        }
      }

      val oneWayParam = Map("one-way" -> oneWay) collect {
        case (k, Some(v)) => (k, v.toJson)
      }

      new JsObject(requiredParams ++ errorParam ++ oneWayParam)
    }

  }
}
