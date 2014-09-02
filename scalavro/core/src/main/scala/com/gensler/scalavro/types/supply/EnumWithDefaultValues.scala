package com.gensler.scalavro.types.supply

import java.lang.reflect.Method

trait AllowUnspecifiedValues[T]{
  _: EnumWithDefaultValues[T] =>
}

trait AllowValuesSet[T]{
  _: EnumWithDefaultValues[T] =>
  def joinValues(values: Set[T]): T
  def divideValue(value: T): Set[T]
}

trait EnumWithDefaultValues[T]{
  self =>

  var nextId = 0
  abstract class Value(val data: T){
    override def equals(obj: Any) = obj match {
      case v: Value => v.data.equals(this.data)
    }
  }

  case class Predefined(override val data: T, id: Int) extends Value(data){
    override def toString = values.find(_._2.id == this.id).get._1
  }

  case class Unresolved(override val data: T) extends Value(data){
    override def toString = s"Unresolved($data)"
  }

  case class Unspecified(override val data: T) extends Value(data){
    override def toString = s"Unspecified($data)"
  }

  case class ValuesSet(values: Set[Predefined]) extends Value(self.asInstanceOf[AllowValuesSet[T]].joinValues(values.map(_.data))) {
    override def toString = s"Set(${values.mkString(", ")})"
  }

  trait ValueFactory {
    def unresolved(data: T): Unresolved
    def unspecified(data: T): Unspecified
    def set(data: T): ValuesSet
  }

  private[scalavro] object ValueFactory extends ValueFactory{

     def next(data: T) = {
      nextId += 1
      Predefined(data, nextId)
    }

    def unresolved(data: T) = Unresolved(data)

    def unspecified(data: T) = self match {
      case _: AllowUnspecifiedValues[T] => Unspecified(data)
      case _ => throw new RuntimeException("This enumeration doesn't support unspecified values")
    }

    def set(data: T) = self match {
      case e: AllowValuesSet[T] =>
        val v = e.divideValue(data).map(d => values.values.filter(_.data == d))
        if(v.forall(_.size == 1)) ValuesSet(v.map(_.head))
        else throw new IllegalArgumentException("Data contains an undefined member")
      case _ => throw new RuntimeException("This enumeration doesn't support sets of values")
    }
  }

  protected def value(data: T): Predefined = {
    populate = true
    ValueFactory.next(data)
  }

  lazy val values = { if(populate) populateNameMap(); nmap }
  private val nmap = scala.collection.mutable.Map[String, Predefined]()
  private var populate = true

  // This method is mostly stolen from Enumeration.scala
  private def populateNameMap() = {
    val fields = getClass.getDeclaredFields
    def isValDef(m: Method) = fields exists (fd => fd.getName == m.getName && fd.getType == m.getReturnType)

    // The list of possible Value methods: 0-args which return a conforming type
    val methods = getClass.getMethods filter (m =>
      m.getParameterTypes.isEmpty &&
      classOf[Predefined].isAssignableFrom(m.getReturnType) &&
      m.getDeclaringClass != classOf[Enumeration] &&
      isValDef(m))

    methods foreach { m =>
      val name = m.getName
      val value = m.invoke(this).asInstanceOf[Predefined]
      nmap(name) = value
    }
    populate = false
  }
}
