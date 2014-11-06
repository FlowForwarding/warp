/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13

import scala.util.Try

import spire.math.UByte

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.supply.{RawSeq, UInt32, UInt8}
import com.gensler.scalavro.io.complex.AvroBareUnionIO

import org.flowforwarding.warp.controller.driver_interface._

import org.flowforwarding.warp.protocol._
import org.flowforwarding.warp.protocol.ofp13.structures._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_header
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_switch_features_reply
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_switch_features_request
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_switch_config_reply
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_switch_config_request
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_set_config

// Currently based on 1.3.3
class Ofp13Impl extends StaticDriver[ofp13_messages.All, Ofp13Impl]{
  val versionCode: UByte = UByte(4)

  protected val msgTypeIO = AvroType[ofp13_messages.TaggedOfp13Message].io.asInstanceOf[AvroBareUnionIO[ofp13_messages.All, ofp13_messages.All]]

  protected val getDPIDField = (m: Any) => m.asInstanceOf[ofp_switch_features_reply].datapathId

  protected val getXidField = (m: Any) => m.asInstanceOf[{ def header: ofp_header }].header.xid

  override protected val getIncomingMessageType = (m: Any) => m match {
    case x: ofp_hello                    => Async
    case x: ofp_error_msg                => Async
    case x: echo_request                 => Request
    case x: echo_reply                   => SingleMessageResponse
    case x: ofp_experimenter_header      => Request // TODO: clarify
    case x: ofp_switch_features_request  => Request
    case x: ofp_switch_features_reply    => SingleMessageResponse
    case x: ofp_switch_config_request    => Request
    case x: ofp_switch_config_reply      => SingleMessageResponse
    case x: ofp_set_config               => Command

    case x: ofp_packet_in                => Async
    case x: ofp_flow_removed             => Async
    case x: ofp_port_status              => Async

    case x: ofp_packet_out               => Command
    case x: ofp_flow_mod                 => Command
    case x: ofp_group_mod                => Command
    case x: ofp_port_mod                 => Command
    case x: ofp_table_mod                => Command

    case x: ofp_multipart_request        => Request
    case x: ofp_multipart_reply          => MultipartResponse(x.flags == ofp_multipart_reply_flags.OFPMPF_REPLY_MORE)

    case x: ofp_barrier_request          => Request
    case x: ofp_barrier_reply            => SingleMessageResponse

    case x: ofp_queue_get_config_request => Request
    case x: ofp_queue_get_config_reply   => SingleMessageResponse

    case x: ofp_role_request             => Request
    case x: ofp_role_reply               => SingleMessageResponse

    case x: ofp_get_async_request        => Request
    case x: ofp_get_async_reply          => SingleMessageResponse
    case x: ofp_set_async                => Command
    case x: ofp_meter_mod                => Request
  }

  protected def transformValue[U](transform: UnionMemberTransform[ofp13_messages.All, U])(value: Any) = Try {
    value match {
      case x: ofp_hello                    => transform(x)
      case x: ofp_error_msg                => transform(x)
      case x: echo_request                 => transform(x)
      case x: echo_reply                   => transform(x)
      case x: ofp_experimenter_header      => transform(x)
      case x: ofp_switch_features_request  => transform(x)
      case x: ofp_switch_features_reply    => transform(x)
      case x: ofp_switch_config_request    => transform(x)
      case x: ofp_switch_config_reply      => transform(x)
      case x: ofp_set_config               => transform(x)

      case x: ofp_packet_in                => transform(x)
      case x: ofp_flow_removed             => transform(x)
      case x: ofp_port_status              => transform(x)

      case x: ofp_packet_out               => transform(x)
      case x: ofp_flow_mod                 => transform(x)
      case x: ofp_group_mod                => transform(x)
      case x: ofp_port_mod                 => transform(x)
      case x: ofp_table_mod                => transform(x)

      case x: ofp_multipart_request        => transform(x)
      case x: ofp_multipart_reply          => transform(x)

      case x: ofp_barrier_request          => transform(x)
      case x: ofp_barrier_reply            => transform(x)

      case x: ofp_queue_get_config_request => transform(x)
      case x: ofp_queue_get_config_reply   => transform(x)

      case x: ofp_role_request             => transform(x)
      case x: ofp_role_reply               => transform(x)

      case x: ofp_get_async_request        => transform(x)
      case x: ofp_get_async_reply          => transform(x)
      case x: ofp_set_async                => transform(x)

      case x: ofp_meter_mod                => transform(x)
    }
  }

  def getHelloMessage(supportedVersions: Array[UByte]): Array[Byte] = {
    val l = supportedVersions.map(1 << _.toShort).reduce(_ | _)
    val hello = ofp_hello(0, ofp_hello_elem_versionbitmap(l))
    encodeUnionMember(hello)
  }

  def rejectVersionError(reason: String): Array[Byte] = {
    val data = reason.getBytes
    val error = ofp_error_msg(
      ofp_header(OFPL_ERROR_LEN + data.length, UInt32()),
      ofp_error_type.OFPET_HELLO_FAILED,
      ofp_error_code.OFPHFC_INCOMPATIBLE,
      RawSeq(data map UInt8.fromByte: _*))
    encodeUnionMember(error)
  }

  def getFeaturesRequest: Array[Byte] = encodeUnionMember(ofp_switch_features_request(0))
}

object Ofp13 extends Ofp13Impl