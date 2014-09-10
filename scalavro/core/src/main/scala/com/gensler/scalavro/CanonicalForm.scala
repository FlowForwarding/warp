package com.gensler.scalavro

/**
  * Mix-in for types that support a JSON "parsing canonical form".
  */
trait CanonicalForm {
  def parsingCanonicalForm(): spray.json.JsValue
}