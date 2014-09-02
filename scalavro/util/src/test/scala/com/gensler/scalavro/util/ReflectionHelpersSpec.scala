package com.gensler.scalavro.util.test

import org.scalatest.{Matchers, FlatSpec}

import scala.reflect.runtime.universe._

import com.gensler.scalavro.util.ReflectionHelpers._

class ReflectionHelpersSpec extends FlatSpec with Matchers {

  object Direction extends Enumeration {
    type Direction = Value
    val NORTH, EAST, SOUTH, WEST = Value
  }

  "The reflection helpers object" should "return the enumeration tag for a given enum value" in {
    val et = enumForValue[Direction.type#Value]
    et.tpe =:= typeOf[Direction.type] should be (true)
  }

  it should "return constructor parameters for case classes" in {
    import scala.collection.immutable.ListMap
    caseClassParamsOf[Animal] should be (ListMap("sound" -> typeTag[String]))
  }

  it should "return constructor parameters for case classes with multiple constructors" in {
    import scala.collection.immutable.ListMap
    caseClassParamsOf[Person] should have size (2)
  }

  it should "return the avro-typable subtypes of a given type" in {
    typeableSubTypesOf[A] should have size (2)
    typeableSubTypesOf[B] should have size (1)
  }

}