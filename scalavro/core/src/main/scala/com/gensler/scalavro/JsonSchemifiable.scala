package com.gensler.scalavro

/**
  * Mix-in for types that support a JSON "schema".
  */
trait JsonSchemifiable {
  def schema(): spray.json.JsValue
}