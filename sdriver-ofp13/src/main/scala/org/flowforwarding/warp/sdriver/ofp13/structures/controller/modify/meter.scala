/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.util.Union._
import com.gensler.scalavro.types.supply._

import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_meter_band_type.OFP_METER_BAND_TYPE
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_meter.OFP_METER
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_meter_mod_command.OFP_METER_MOD_COMMAND
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_meter_flags.OFP_METER_FLAGS
import org.flowforwarding.warp.sdriver.dynamic.DynamicPath
import ofp_length._

/* Meter configuration. */
case class ofp_meter_mod private [sdriver] (
  header: ofp_header,
  command: OFP_METER_MOD_COMMAND, /* One of OFPMC_*. */
  flags: OFP_METER_FLAGS,         /* Bitmap of OFPMF_* flags. */
  meter_id: OFP_METER,            /* Meter instance. */
  bands: RawSeq[ofp_meter_band])  /* The band list length is inferred from the length field in the header. */

object ofp_meter_mod extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 4 => bodyLengthMinus(8) }

  private[sdriver] def build(@DynamicPath("header", "xid") xid: UInt32,
                              command: OFP_METER_MOD_COMMAND,
                              flags: OFP_METER_FLAGS,
                              meter_id: OFP_METER,
                              bands: RawSeq[ofp_meter_band])  = {
    val header = ofp_header(OFPL_METER_MOD_LEN + bands.map(b => UInt16.toShort(b.header.len)).sum, xid)
    ofp_meter_mod(header, command, flags, meter_id, bands)
  }
}

object ofp_meter_bands{
  type All = union [ofp_meter_band_drop]        #or
                   [ofp_meter_band_dscp_remark] #or
                   [ofp_meter_band_experimenter]
}

trait ofp_meter_band extends WordTaggedUnion[ofp_meter_bands.All, OFP_METER_BAND_TYPE]{
  def header: ofp_meter_band_header
}


/* Meter numbering. Flow meters can use any number up to   val OFPM_MAX. */
object ofp_meter extends DWordEnum with AllowUnspecifiedValues[Int]{
  type OFP_METER = Value

  /* Last usable meter. */
  val OFPM_MAX        = ##(0xffff0000L)
  /* Virtual meters. */
  val OFPM_SLOWPATH   = ##(0xfffffffdL) /* Meter for slow datapath. */
  val OFPM_CONTROLLER = ##(0xfffffffeL) /* Meter for controller connection. */
  val OFPM_ALL        = ##(0xffffffffL) /* Represents all meters for stat requests commands. */
}

/* Meter commands */
object ofp_meter_mod_command extends WordEnum{
  type OFP_METER_MOD_COMMAND = Value

  val OFPMC_ADD    = ##(0) /* New meter. */
  val OFPMC_MODIFY = ##(1) /* Modify specified meter. */
  val OFPMC_DELETE = ##(2) /* Delete specified meter. */
}

/* Meter configuration flags */
object ofp_meter_flags extends WordBitmap{
  type OFP_METER_FLAGS = Value

  val OFPMF_KBPS  = ##(1 << 0) /* Rate value in kb/s (kilo-bit per second). */
  val OFPMF_PKTPS = ##(1 << 1) /* Rate value in packet/sec. */
  val OFPMF_BURST = ##(1 << 2) /* Do burst size. */
  val OFPMF_STATS = ##(1 << 3) /* Collect statistics. */
}

/* Common header for all meter bands */
case class ofp_meter_band_header private [sdriver] (
  len: UInt16,            /* Length in bytes of this band. */
  rate: UInt32,           /* Rate for this band. */
  burst_size: UInt32)     /* Size of bursts. */

/* Meter band types */
object ofp_meter_band_type extends WordEnum{
  type OFP_METER_BAND_TYPE = Value

  val OFPMBT_DROP         = ##(1)       /* Drop packet. */
  val OFPMBT_DSCP_REMARK  = ##(2)       /* Remark DSCP in the IP header. */
  val OFPMBT_EXPERIMENTER = ##(0xffffL) /* Experimenter meter band. */
}

/* OFPMBT_DROP band - drop packets */
case class ofp_meter_band_drop private [sdriver] (header: ofp_meter_band_header, pad: Pad4) extends ofp_meter_band

object ofp_meter_band_drop{
  private[sdriver] def build(@DynamicPath("header", "rate") rate: UInt32,
                              @DynamicPath("header", "burst_size") burst_size: UInt32)  = {
    val header = ofp_meter_band_header(UInt16.fromShort(16), rate, burst_size)
    ofp_meter_band_drop(header, Pad4())
  }
}

/* OFPMBT_DSCP_REMARK band - Remark DSCP in the IP header */
case class ofp_meter_band_dscp_remark private [sdriver] (header: ofp_meter_band_header, prec_level: UInt8, pad: Pad3) extends ofp_meter_band

object ofp_meter_band_dscp_remark{
  private[sdriver] def build(@DynamicPath("header", "rate") rate: UInt32,
                              @DynamicPath("header", "burst_size") burst_size: UInt32,
                              prec_level: UInt8)  = {
    val header = ofp_meter_band_header(UInt16.fromShort(16), rate, burst_size)
    ofp_meter_band_dscp_remark(header, prec_level, Pad3())
  }
}

/* OFPMBT_EXPERIMENTER band - Write actions in action set */
/* Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
case class ofp_meter_band_experimenter private [sdriver] (header: ofp_meter_band_header, experimenter: UInt32) extends ofp_meter_band

object ofp_meter_band_experimenter{
  private[sdriver] def build(@DynamicPath("header", "rate") rate: UInt32,
                              @DynamicPath("header", "burst_size") burst_size: UInt32,
                              experimenter: UInt32)  = {
    val header = ofp_meter_band_header(UInt16.fromShort(16), rate, burst_size)
    ofp_meter_band_experimenter(header, experimenter)
  }
}
