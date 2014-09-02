package com.gensler.scalavro.util.test

case class Animal(sound: String)

// case class with multiple constructors
case class Person(name: String, age: Int) {
  def this(name: String) = this(name, 0)
}

class A
abstract class B extends A
case class C() extends A
case class D() extends B
