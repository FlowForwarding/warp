package com.gensler.scalavro.test

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import spray.json.{ JsValue, PrettyPrinter }

trait AvroSpec extends FlatSpec with Matchers with StrictLogging {

  protected def prettyPrint(json: JsValue) {
    val buff = new java.lang.StringBuilder
    PrettyPrinter.print(json, buff)
    logger debug buff.toString
  }

}
