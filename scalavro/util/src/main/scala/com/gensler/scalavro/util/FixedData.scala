package com.gensler.scalavro.util

import com.gensler.scalavro

import scala.collection.immutable
import scala.reflect.runtime.universe._
import scala.reflect.runtime.universe.{ Annotation => ReflectedAnnotation }
import scala.annotation.{ StaticAnnotation }

/**
  * The type of fixed-length data.
  *
  * Implementations of this class must be decorated with a `FixedData.Length`
  * annotation.
  *
  * @example
  * {{{
  *   import com.gensler.scalavro.util.FixedData
  *   import scala.collection.immutable
  *
  *   @FixedData.Length(16)
  *   case class MD5(override val bytes: immutable.Seq[Byte])
  *     extends FixedData(bytes)
  * }}}
  *
  * @param bytes The value of this Fixed data element as a sequence of bytes.
  *              The size of this sequence must be exactly that declared in the
  *              `FixedData.Length` annotation.
  */
abstract class FixedData(val bytes: immutable.Seq[Byte]) {

  private def lengthAnnotation: Option[FixedData.Length] = FixedData.lengthAnnotationInstance(
    ReflectionHelpers.classLoaderMirror.classSymbol(this.getClass)
  )

  // ensure subclasses have the Length annotation
  assert(
    lengthAnnotation.isDefined,
    "FixedData subclasses must be decorated with a FixedData.Length annotation."
  )

  // ensure the byte sequence is of proper length
  assert(
    bytes.length == length,
    "The bytes.length must conform to the FixedData.Length annotation."
  )

  /**
    * Returns the length of this fixed-length data element.  This value is
    * derived from the `FixedData.Length` annotation.
    */
  final lazy val length: Int = lengthAnnotation.get.length

}

object FixedData {

  /**
    * Marker annotation class, required for concrete subclasses of
    * [[FixedData]].
    */
  case class Length(length: Int) extends StaticAnnotation

  /**
    * Returns Some(FixedData.Length) if the annotation declared for the supplied type.
    */
  private[scalavro] def lengthAnnotationInstance(classSymbol: ClassSymbol): Option[FixedData.Length] = {
    if (!(classSymbol.toType <:< typeOf[FixedData])) None
    else {
      classSymbol.annotations.find { _.tree.tpe =:= typeOf[FixedData.Length] }.map { lengthAnnotation =>
        val lengthSymbol = ReflectionHelpers.classLoaderMirror.classSymbol(classOf[FixedData.Length])
        val lengthMirror = ReflectionHelpers.classLoaderMirror reflectClass lengthSymbol
        val lengthConstructorSymbol = typeOf[FixedData.Length].decl(termNames.CONSTRUCTOR).asMethod
        val lengthConstructorMirror = lengthMirror reflectConstructor lengthConstructorSymbol
        val annotationArguments = lengthAnnotation.scalaArgs.map { _.productElement(0).asInstanceOf[Constant].value }
        lengthConstructorMirror(annotationArguments: _*).asInstanceOf[FixedData.Length]
      }
    }
  }

}