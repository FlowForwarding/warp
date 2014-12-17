/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.Union.union

object ofp_instruction_type extends WordEnum {
  type OFP_INSTRUCTION_TYPE = Value

  val OFPIT_GOTO_TABLE     = ##(1)       /* Setup the next table in the lookup pipeline */
  val OFPIT_WRITE_METADATA = ##(2)       /* Setup the metadata field for use later in pipeline */
  val OFPIT_WRITE_ACTIONS  = ##(3)       /* Write the action(s) onto the datapath action set */
  val OFPIT_APPLY_ACTIONS  = ##(4)       /* Applies the action(s) immediately */
  val OFPIT_CLEAR_ACTIONS  = ##(5)       /* Clears all actions from the datapath action set */
  val OFPIT_METER          = ##(6)       /* Apply meter (rate limiter) */
  val OFPIT_EXPERIMENTER   = ##(0xffffL) /* Experimenter instruction */
}

object instruction_length extends WordEnum with AllowUnspecifiedValues[Short]{
  type INSTRUCTION_LENGTH = Value

  val IL_GOTO_TABLE          = ##(8)
  val IL_WRITE_METADATA      = ##(24)
  val IL_METER               = ##(8)
  val IL_ACTIONS: Value      = ##(8)
  val IL_EXPERIMENTER: Value = ##(8)

  def IL_ACTIONS(actions: RawSeq[ofp_action]): Value = Unspecified((IL_ACTIONS.data + actions.foldLeft(0) { _ + _.len.data }).toShort)
  def IL_EXPERIMENTER(dataLength: Int): Value        = Unspecified((IL_EXPERIMENTER.data + dataLength).toShort)
}

import ofp_instruction_type._
import instruction_length._

object ofp_instructions {
  type All = union [ofp_instruction_goto_table]     #or
                   [ofp_instruction_write_metadata] #or
                   [ofp_instruction_write_actions]  #or
                   [ofp_instruction_apply_actions]  #or
                   [ofp_instruction_clear_actions]  #or
                   [ofp_instruction_meter]          #or
                   [ofp_instruction_experimenter]
}

trait ofp_instruction extends WordTaggedUnion[ofp_instructions.All, OFP_INSTRUCTION_TYPE]{
  def length: INSTRUCTION_LENGTH
}

case class ofp_instruction_goto_table private[sdriver] (
  length: INSTRUCTION_LENGTH,
  table_id: UInt8,
  pad: Pad3) extends ofp_instruction

object ofp_instruction_goto_table {
  private [sdriver] def build(table_id: UInt8) = ofp_instruction_goto_table(IL_GOTO_TABLE, table_id, Pad3())
}

case class ofp_instruction_write_metadata private[sdriver] (
  length: INSTRUCTION_LENGTH,
  pad: Pad4,                                     /* Align to 64-bits */
  metadata: UInt64,                              /* Metadata value to write */
  metadata_mask: UInt64) extends ofp_instruction /* Metadata write bitmask */

object ofp_instruction_write_metadata {
  private [sdriver] def build(metadata: UInt64, metadata_mask: UInt64) = ofp_instruction_write_metadata(IL_WRITE_METADATA, Pad4(), metadata, metadata_mask)
}

abstract class ofp_instruction_actions_companion[T <: ofp_instruction](
  constructor: (INSTRUCTION_LENGTH, Pad4, RawSeq[ofp_action]) => T) extends RawSeqFieldsInfo{

  private [sdriver] def build(actions: RawSeq[ofp_action]) = constructor(IL_ACTIONS(actions), Pad4(), actions)

  val rawFieldsLengthCalculator: LengthCalculator = { case 2 => Some {
    case Seq(x: instruction_length.Value, _*) => x.data - IL_ACTIONS.data
  }}
}

case class ofp_instruction_write_actions private[sdriver] (
  length: INSTRUCTION_LENGTH,
  pad: Pad4,                                           /* Align to 64-bits */
  actions: RawSeq[ofp_action]) extends ofp_instruction /* 0 or more actions associated with OFPIT_WRITE_ACTIONS and OFPIT_APPLY_ACTIONS */

object ofp_instruction_write_actions extends ofp_instruction_actions_companion(new ofp_instruction_write_actions(_, _, _))

case class ofp_instruction_apply_actions private[sdriver] (
  length: INSTRUCTION_LENGTH,
  pad: Pad4,                                           /* Align to 64-bits */
  actions: RawSeq[ofp_action]) extends ofp_instruction /* 0 or more actions associated with OFPIT_WRITE_ACTIONS and OFPIT_APPLY_ACTIONS */

object ofp_instruction_apply_actions extends ofp_instruction_actions_companion(new ofp_instruction_apply_actions(_, _, _))

case class ofp_instruction_clear_actions private[sdriver] (
  length: INSTRUCTION_LENGTH,
  pad: Pad4,                                           /* Align to 64-bits */
  actions: RawSeq[ofp_action]) extends ofp_instruction /* 0 or more actions associated with OFPIT_WRITE_ACTIONS and OFPIT_APPLY_ACTIONS */

object ofp_instruction_clear_actions extends ofp_instruction_actions_companion(new ofp_instruction_clear_actions(_, _, _))

case class ofp_instruction_meter private[sdriver] (
  length: INSTRUCTION_LENGTH,
  meter_id: UInt32) extends ofp_instruction

object ofp_instruction_meter {
  private [sdriver] def build(meter_id: UInt32) = ofp_instruction_meter(IL_METER, meter_id)
}

case class ofp_instruction_experimenter private[sdriver] (
  length: INSTRUCTION_LENGTH,
  experimenter: UInt32,                        /* Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
  data: RawSeq[UInt8]) extends ofp_instruction /* Experimenter-defined arbitrary additional data. */

object ofp_instruction_experimenter extends RawSeqFieldsInfo {
  private [sdriver] def build(experimenter: UInt32, data: RawSeq[UInt8]) = ofp_instruction_experimenter(IL_EXPERIMENTER(data.length), experimenter, data)

  val rawFieldsLengthCalculator: LengthCalculator = { case 2 => Some {
    case Seq(x: instruction_length.Value, _*) => x.data - IL_EXPERIMENTER.data
  }}
}