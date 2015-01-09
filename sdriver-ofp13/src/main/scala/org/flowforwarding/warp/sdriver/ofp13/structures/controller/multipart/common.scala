/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import scala.reflect.runtime.universe._
import scala.reflect.api

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.Union._
import org.flowforwarding.warp.sdriver.dynamic.DynamicPath
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_request_flags.OFP_MULTIPART_REQUEST_FLAGS
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_reply_flags.OFP_MULTIPART_REPLY_FLAGS

object ofp_multipart_request_flags extends WordBitmap{
  type OFP_MULTIPART_REQUEST_FLAGS = Value
  val OFPMPF_REQ_MORE = ##(1 << 0) /* More requests to follow. */
}

object ofp_multipart_reply_flags extends WordBitmap{
  type OFP_MULTIPART_REPLY_FLAGS = Value
  val OFPMPF_REPLY_MORE = ##(1 << 0) /* More replies to follow. */
}

object ofp_multipart_type extends WordEnum{
  type OFP_MULTIPART_TYPE = Value

  /* Description of this OpenFlow switch.
   * The request body is empty.
   * The reply body is struct ofp_desc. */
  val OFPMP_DESC           = ##(0)
  /* Individual flow statistics.
   * The request body is struct ofp_flow_stats_request.
   * The reply body is an array of struct ofp_flow_stats. */
  val OFPMP_FLOW           = ##(1)
  /* Aggregate flow statistics.
    * The request body is struct ofp_aggregate_stats_request.
    * The reply body is struct ofp_aggregate_stats_reply. */
  val OFPMP_AGGREGATE      = ##(2)
  /* Flow table statistics.
    * The request body is empty.
    * The reply body is an array of struct ofp_table_stats. */
  val OFPMP_TABLE          = ##(3)
  /* Port statistics.
   * The request body is struct ofp_port_stats_request.
    * The reply body is an array of struct ofp_port_stats. */
  val OFPMP_PORT_STATS     = ##(4)
  /* Queue statistics for a port
   * The request body is struct ofp_queue_stats_request.
   * The reply body is an array of struct ofp_queue_stats */
  val OFPMP_QUEUE          = ##(5)
  /* Group counter statistics.
   * The request body is struct ofp_group_stats_request.
   * The reply is an array of struct ofp_group_stats. */
  val OFPMP_GROUP          = ##(6)
  /* Group description.
   * The request body is empty.
   * The reply body is an array of struct ofp_group_desc. */
  val OFPMP_GROUP_DESC     = ##(7)
  /* Group features.
   * The request body is empty.
   * The reply body is struct ofp_group_features. */
  val OFPMP_GROUP_FEATURES = ##(8)
  /* Meter statistics.
   * The request body is struct ofp_meter_multipart_requests.
   * The reply body is an array of struct ofp_meter_stats. */
  val OFPMP_METER          = ##(9)
  /* Meter configuration.
   * The request body is struct ofp_meter_multipart_requests.
   * The reply body is an array of struct ofp_meter_config. */
  val OFPMP_METER_CONFIG   = ##(10)
  /* Meter features.
   * The request body is empty.
   * The reply body is struct ofp_meter_features. */
  val OFPMP_METER_FEATURES = ##(11)
  /* Table features.
   * The request body is either empty or contains an array of
   * struct ofp_table_features containing the controller's
   * desired view of the switch. If the switch is unable to
   * set the specified view an error is returned.
   * The reply body is an array of struct ofp_table_features. */
  val OFPMP_TABLE_FEATURES = ##(12)
  /* Port description.
   * The request body is empty.
   * The reply body is an array of struct ofp_port. */
  val OFPMP_PORT_DESC      = ##(13)
  /* Experimenter extension.
   * The request and reply bodies begin with
   * struct ofp_experimenter_multipart_header.
   * The request and reply bodies are otherwise experimenter-defined. */
  val OFPMP_EXPERIMENTER   = ##(0xffffL)
}

import ofp_length._
import ofp_multipart_type._

trait MultipartRequest[T]{
  def tp: OFP_MULTIPART_TYPE
  def structures: Seq[T]
}

trait EmptyMultipartRequest extends MultipartRequest[Nothing]{
  def structures = Seq.empty
}

object ofp_multipart_request extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = {
    case 4 => bodyLengthMinus(8)
  }

  def classToTypeTag[A](c: Class[_]): scala.reflect.runtime.universe.TypeTag[A] = {
    val mirror = runtimeMirror(c.getClassLoader)  // obtain runtime mirror
    val sym = mirror.staticClass(c.getName)  // obtain class symbol for `c`
    val tpe = sym.selfType  // obtain type object for `c`
    // create a type tag which contains above type object
    TypeTag(mirror, new api.TypeCreator {
      def apply[U <: api.Universe with Singleton](m: api.Mirror[U]) =
        if (m eq mirror) tpe.asInstanceOf[U # Type]
        else throw new IllegalArgumentException(s"Type tag defined in $mirror cannot be migrated to other mirrors.")
    })
  }


  private[sdriver] def build(@DynamicPath("header", "xid") xid: UInt32, flags: ofp_multipart_request_flags.OFP_MULTIPART_REQUEST_FLAGS, body: MultipartRequest[_]) = {
    val bytes: Array[Byte] = if(body.structures.nonEmpty) {
      val ostream = new ByteArrayOutputStream
      val io = AvroType(classToTypeTag[Any](body.structures.head.getClass)).io
      body.structures.foreach { s => io.write(s, ostream) }
      ostream.toByteArray
    }
    else Array.empty
    val header = ofp_header(OFPL_MULTIPART_REQUEST_LEN + bytes.length , xid)
    ofp_multipart_request(header, body.tp, flags, Pad4(), RawSeq(bytes map UInt8.fromByte: _*))
  }
}

case class ofp_multipart_request private[sdriver] (header: ofp_header,
                                                    tp: OFP_MULTIPART_TYPE,
                                                    flags: ofp_multipart_request_flags.OFP_MULTIPART_REQUEST_FLAGS,
                                                    pad: Pad4,
                                                    rawBody: RawSeq[UInt8])

object ofp_multipart_reply extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = {
    case 4 => bodyLengthMinus(8)
  }
}

case class ofp_multipart_reply private[sdriver] (header: ofp_header,
                                                  tp: OFP_MULTIPART_TYPE,
                                                  flags: ofp_multipart_reply_flags.OFP_MULTIPART_REPLY_FLAGS,
                                                  pad: Pad4,
                                                  rawBody: RawSeq[UInt8]){

  private lazy val iarray = (rawBody map UInt8.toByte).toArray
  private def read[A: TypeTag]: A = AvroType[A].io.read(new ByteArrayInputStream(iarray)).get

  /* I'm pretty sure anyone needs structured representation of data, not just a raw array of bytes.
     Raw data are accessible using rawBody method.
   */
  def body: Any = tp match {
    case OFPMP_DESC           => ofp_multipart_desc_reply           (read[       ofp_desc                   ])
    case OFPMP_FLOW           => ofp_multipart_flow_stats_reply     (read[RawSeq[ofp_flow_stats            ]])
    case OFPMP_AGGREGATE      => ofp_multipart_aggregate_stats_reply(read[       ofp_aggregate_stats        ])
    case OFPMP_TABLE          => ofp_multipart_table_stats_reply    (read[RawSeq[ofp_table_stats           ]])
    case OFPMP_PORT_STATS     => ofp_multipart_port_stats_reply     (read[       ofp_port_stats             ])
    case OFPMP_QUEUE          => ofp_multipart_queue_stats_reply    (read[RawSeq[ofp_queue_stats           ]])
    case OFPMP_GROUP          => ofp_multipart_group_stats_reply    (read[RawSeq[ofp_group_stats           ]])
    case OFPMP_GROUP_DESC     => ofp_multipart_group_desc_reply     (read[RawSeq[ofp_group_desc            ]])
    case OFPMP_GROUP_FEATURES => ofp_multipart_group_features_reply (read[       ofp_group_features         ])
    case OFPMP_METER          => ofp_multipart_meter_stats_reply    (read[RawSeq[ofp_meter_stats           ]])
    case OFPMP_METER_CONFIG   => ofp_multipart_meter_config_reply   (read[RawSeq[ofp_meter_config          ]])
    case OFPMP_METER_FEATURES => ofp_multipart_meter_features_reply (read[       ofp_meter_features         ])
    case OFPMP_TABLE_FEATURES => ofp_multipart_table_features_reply (read[RawSeq[ofp_table_features        ]])
    case OFPMP_PORT_DESC      => ofp_multipart_port_desc_reply      (read[RawSeq[ofp_port                  ]])
    case OFPMP_EXPERIMENTER   => ofp_multipart_experimenter_reply   (read[       ofp_experimenter_multipart ])
  }
}
