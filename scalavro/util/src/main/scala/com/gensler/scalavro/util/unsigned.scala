package com.gensler.scalavro
package util

object U8 {
  def f(i: Byte): Short = (i.toShort & 0xff).toShort
  def t(l: Short): Byte = l.toByte
}

object U16 {
  def f(i: Short): Int = i.toInt & 0xffff
  def t(l: Int): Short = l.toShort
}


object U32 {
  def f(i: Int): Long = i.toLong & 0xffffffff
  def t(l: Long): Int = l.toInt
}

