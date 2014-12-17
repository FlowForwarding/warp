/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.demo

import scala.util.{Success, Failure}
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.io.IO

import spray.can.Http
import spray.client.pipelining._
import spray.json._

import spray.httpx.SprayJsonSupport._
import spray.util._

import org.flowforwarding.warp.controller.modules.rest.SendToSwitchRequest
import org.flowforwarding.warp.controller.modules.rest.SendToSwitchService._
import spire.math.{UByte, ULong}

object RestClient extends App {
  implicit val system = ActorSystem("rest-client")
  import system.dispatcher // execution context for futures below

  val pipeline = sendReceive

  //val req = """{ "EchoRequest": { "elements": [5, 5, 5, 5] } }"""
  //val req = """ { "PortMod": { "port_no": 6633, "hw_addr": "01:02:03:04:05:06", "config": 1, "mask": 1, "advertise": 1 } } """
  val req = """
{
  "FlowMod": {
    "cookie": 1,
    "cookie_mask": 0,
    "table_id": 0,
    "command": 0,
    "idle_timeout": 1,
    "hard_timeout": 5,
    "priority": 100,
    "buffer_id": 30,
    "out_port": 333,
    "out_group": 7,
    "flags": 1,
    "match": { "Match": { "oxm": true, "fields": [] } },
    "instructions" :
    [
      {
        "InstructionWriteActions": {
          "actions": [ { "ActionGroup":    { "group_id": 10 } },
                       { "ActionPushVlan": { "ethertype": 1 } },
                       { "ActionSetField": { "field": { "OxmTlv": { "field": "vlan_vid",       "value": 258               } } } },
                       { "ActionSetField": { "field": { "OxmTlv": { "field": "vlan_vid",       "value": 258, "mask": 230  } } } },
                       { "ActionSetField": { "field": { "OxmTlv": { "field": "ipv6_nd_target", "value": "127.0.0.1"       } } } } ]


        }
      }
    ]
  }
}"""

  val responseFuture = pipeline {
    Post("http://127.0.0.1:8080/rest/send", SendToSwitchRequest(ULong(args.lift(0).get), UByte(4), false, req.parseJson.asJsObject))
  }

  responseFuture onComplete {
    case Success(resp) =>
      println("The call was successful: ", resp)
      shutdown()

    case Failure(error) =>
      println(error, "Error")
      shutdown()
  }

  def shutdown(): Unit = {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
}