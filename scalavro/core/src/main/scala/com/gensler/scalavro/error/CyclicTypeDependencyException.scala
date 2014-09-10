package com.gensler.scalavro.error

class CyclicTypeDependencyException(msg: String) extends RuntimeException(msg)