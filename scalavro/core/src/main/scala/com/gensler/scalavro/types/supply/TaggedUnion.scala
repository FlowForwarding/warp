package com.gensler.scalavro.types.supply

import scala.reflect.runtime.universe.TypeTag
import com.gensler.scalavro.util.Union

abstract class TaggedUnion[U <: Union.not[_]: TypeTag, Elem, E <: EnumWithDefaultValues[Elem]#Value: TypeTag]
abstract class ByteTaggedUnion[U <: Union.not[_]: TypeTag, E <: EnumWithDefaultValues[Byte]#Value: TypeTag] extends TaggedUnion[U, Byte, E]
abstract class WordTaggedUnion[U <: Union.not[_]: TypeTag, E <: EnumWithDefaultValues[Short]#Value: TypeTag] extends TaggedUnion[U, Short, E]
abstract class DWordTaggedUnion[U <: Union.not[_]: TypeTag, E <: EnumWithDefaultValues[Int]#Value: TypeTag] extends TaggedUnion[U, Int, E]
