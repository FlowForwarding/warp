package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.Union._
import com.gensler.scalavro.util.U8

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_queue_property.OFP_QUEUE_PROPERTY

object ofp_queue_property extends WordEnum {
  type OFP_QUEUE_PROPERTY = Value

  val OFPQT_MIN_RATE     = ##(1)       /* Minimum datarate guaranteed. */
  val OFPQT_MAX_RATE     = ##(2)       /* Maximum datarate. */
  val OFPQT_EXPERIMENTER = ##(0xffffL) /* Experimenter defined property. */
}

object Queue{
  val OFPQ_MIN_RATE_UNCFG = UInt16(U8.t(0xff), U8.t(0xff))
  val OFPQ_MAX_RATE_UNCFG = UInt16(U8.t(0xff), U8.t(0xff))
}

object ofp_queue_properties{
  type All = union [ofp_queue_prop_min_rate]    #or
                   [ofp_queue_prop_max_rate]    #or
                   [ofp_queue_prop_experimenter]
}

trait ofp_queue_property extends WordTaggedUnion[ofp_queue_properties.All, OFP_QUEUE_PROPERTY]{
  def prop_header: ofp_queue_prop_header
}

/* Full description for a queue. */
case class ofp_packet_queue private[protocol] (
  queue_id: UInt32, /* id for the specific queue. */
  port: UInt32,     /* Port this queue is attached to. */
  len: UInt16,      /* Length in bytes of this queue desc. */
  pad: Pad6,        /* 64-bit alignment. */
  properties: RawSeq[ofp_queue_property])

object ofp_packet_queue extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 4 => {
    case Seq(_, _, len: UInt16, _*) => UInt16.toShort(len) - 16
  }}
}

/* Common description for a queue. */
case class ofp_queue_prop_header private[protocol] (len: UInt16, pad: Pad4)

/* Min-Rate queue property description. */
case class ofp_queue_prop_min_rate private[protocol] (
  prop_header: ofp_queue_prop_header, /* prop: OFPQT_MIN, len: 16. */
  rate: UInt16,                       /* In 1/10 of a percent; >1000 -> disabled. */
  pad: Pad6) extends ofp_queue_property

/* Max-Rate queue property description. */
case class ofp_queue_prop_max_rate private[protocol] (
  prop_header: ofp_queue_prop_header, /* prop: OFPQT_MAX, len: 16. */
  rate: UInt16,                       /* In 1/10 of a percent; >1000 -> disabled. */
  pad: Pad6)  extends ofp_queue_property


/* Experimenter queue property description. */
case class ofp_queue_prop_experimenter private[protocol] (
  prop_header: ofp_queue_prop_header,             /* prop: OFPQT_EXPERIMENTER, len: 16. */
  experimenter: UInt32,                           /* Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
  pad: Pad4,                                      /* 64-bit alignment */
  data: RawSeq[UInt8]) extends ofp_queue_property /* Experimenter defined data. */


object ofp_queue_prop_experimenter extends RawSeqFieldsInfo {
  val rawFieldsLengthCalculator: LengthCalculator = { case 3 => {
    case Seq(h: ofp_queue_prop_header, _*) => UInt16.toShort(h.len) - 16
  }}
}

