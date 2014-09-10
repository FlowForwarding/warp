package com.gensler.scalavro.test

import com.gensler.scalavro.protocol._

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.protocol.AvroProtocol

class AvroProtocolSpec extends AvroSpec {

  "The Scalavro Protocol Support" should "provide conforming handshake record types" in {
    val handshakeRequestType = AvroType[HandshakeRequest]
    val handshakeResponseType = AvroType[HandshakeResponse]

    // prettyPrint(handshakeRequestType.selfContainedSchema())
    // prettyPrint(handshakeResponseType.selfContainedSchema())
  }

  it should "construct protocol definitions" in {

    import com.gensler.scalavro.util.Union._

    val greetingType = AvroType[Greeting].asInstanceOf[AvroRecord[Greeting]]
    val curseType = AvroType[Curse].asInstanceOf[AvroRecord[Curse]]

    val hwProtocol = AvroProtocol(

      protocol = "HelloWorld",

      types = Seq(greetingType, curseType),

      messages = Map(
        "hello" -> AvroProtocol.Message(

          request = greetingType,

          response = greetingType,

          errors = AvroType.fromType[union[Curse]#apply].map{
            _.asInstanceOf[AvroUnion[_, _]]
          }.toOption,

          doc = Some("Say hello.")
        )
      ),

      namespace = Some("com.gensler.scalavro.tests"),

      doc = Some("Protocol Greetings")

    )

    // log.debug(hwProtocol.schema.prettyPrint)
    // log.debug(hwProtocol.parsingCanonicalForm.prettyPrint)
  }

}
