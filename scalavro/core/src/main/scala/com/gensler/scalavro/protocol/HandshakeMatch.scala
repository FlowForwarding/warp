package com.gensler.scalavro.protocol

object HandshakeMatch extends Enumeration {
  type HandshakeMatch = Value
  val BOTH, CLIENT, NONE = Value
}
