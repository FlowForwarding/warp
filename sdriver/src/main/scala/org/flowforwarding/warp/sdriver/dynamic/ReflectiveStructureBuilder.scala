/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.dynamic

import scala.util.{Failure, Success, Try}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types.supply.RawSeq
import org.flowforwarding.warp.driver_api.dynamic._

class DynamicPath(val path: String*) extends scala.annotation.StaticAnnotation

private class ReflectiveStructureBuilder(underlyingClass: Class[_], builder: DynamicStructureBuilder[ReflectiveStructure]){

  private val classLoaderMirror  = runtimeMirror(getClass.getClassLoader) // maybe it must be parameter
  private val classSymbol        = classLoaderMirror.classSymbol(underlyingClass)
  private val companion          = classSymbol.companion.asModule
  private val companionInstance  = classLoaderMirror reflect (classLoaderMirror reflectModule companion).instance

  private val buildMethodSymbol  = methodByName(companionInstance, "build")

  private val buildParamsSymbols = buildMethodSymbol.paramLists.headOption.getOrElse(List.empty)
  private val buildParamsNames   = buildParamsSymbols map { _.name.toString }
  private val buildParamsTypes: Map[String, (Type, Seq[String])] = buildParamsSymbols map {
    sym => {
      val path = sym.asTerm.annotations.collectFirst {
        case info if info.tree.tpe == typeOf[DynamicPath] =>
          info.tree.children.tail.map { case Literal(Constant(value)) => value.toString }
      } getOrElse List.empty
      (sym.name.toString, (sym.typeSignature, path))
    }
  } toMap

  private val buildMethod = companionInstance reflectMethod buildMethodSymbol

  private val dArgs = Try {
    defaultArgsOfMethod(companionInstance, buildMethodSymbol).toSeq.map {
      case (i, value) => (buildParamsNames(i), value)
    }
  }

  private val defaultArgs = Map(dArgs getOrElse Seq.empty: _*)

  private def inputMemberToConstructorsType(name: String, value: DynamicMember): Any = value match {
    case DynamicPrimitive(v) => fromLong(buildParamsTypes(name)._1, v)
    case DynamicPrimitives(vs) =>
      buildParamsTypes(name)._1 match {
        case TypeRef(_, sym, List(rawSeqType)) if sym.asType == typeOf[RawSeq[_]].typeSymbol =>
          val primitives = vs map { fromLong(rawSeqType, _) }
          RawSeq(primitives: _*)
        case t => throw new RuntimeException(s"Type $t can not be initialized by array of primitive types")
      }

    case DynamicStructureInput(v) => builder.build(v).get.underlying
    case DynamicStructureInputs(vs) => RawSeq(vs.map(v => builder.build(v).get.underlying): _*)
  }

  def getBuildArgs(structureInput: DynamicBuilderInput): Seq[Any] = {
    buildParamsNames map { name =>
      val member = Try { buildParamsTypes(name) match {
        case (t, Seq()) => structureInput.getMembers(name)
        case (t, seq) =>
          seq.foldLeft[DynamicMember](DynamicStructureInput(structureInput)) {
            (outerStructure, nestedStructureName) => outerStructure match {
              case DynamicStructureInput(v) => v.getMembers(nestedStructureName)
            }
          }
        }
      }
      member match {
        case Success(value) => inputMemberToConstructorsType(name, value)
        case Failure(_) if defaultArgs.isDefinedAt(name) => defaultArgs(name)
        case Failure(t) => throw new Exception("Unable to construct value from input or extract a default value for field " + name, t)
      }
    }
  }

  def build(structureInput: DynamicBuilderInput): Try[ReflectiveStructure] = Try {
    val instance = buildMethod(getBuildArgs(structureInput): _*)
    new ReflectiveStructure(instance)
  }
}