/*
* © 2013 FlowForwarding.Org
* All Rights Reserved.  Use is subject to license terms.
*
* @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
*/
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.Union.union

/* Table Feature property types.
* Low order bit cleared indicates a property for a regular Flow Entry.
* Low order bit set indicates a property for the Table-Miss Flow Entry.
*/
object ofp_table_feature_prop_type extends WordEnum{
  type OFP_TABLE_FEATURE_PROP_TYPE = Value

  val OFPTFPT_INSTRUCTIONS        = ##(0) /* Instructions property. */
  val OFPTFPT_INSTRUCTIONS_MISS   = ##(1) /* Instructions for table-miss. */
  val OFPTFPT_NEXT_TABLES         = ##(2) /* Next Table property. */
  val OFPTFPT_NEXT_TABLES_MISS    = ##(3) /* Next Table for table-miss. */
  val OFPTFPT_WRITE_ACTIONS       = ##(4) /* Write Actions property. */
  val OFPTFPT_WRITE_ACTIONS_MISS  = ##(5) /* Write Actions for table-miss. */
  val OFPTFPT_APPLY_ACTIONS       = ##(6) /* Apply Actions property. */
  val OFPTFPT_APPLY_ACTIONS_MISS  = ##(7) /* Apply Actions for table-miss. */
  val OFPTFPT_MATCH               = ##(8) /* Match property. */
  val OFPTFPT_WILDCARDS           = ##(10) /* Wildcards property. */
  val OFPTFPT_WRITE_SETFIELD      = ##(12) /* Write Set-Field property. */
  val OFPTFPT_WRITE_SETFIELD_MISS = ##(13) /* Write Set-Field for table-miss. */
  val OFPTFPT_APPLY_SETFIELD      = ##(14) /* Apply Set-Field property. */
  val OFPTFPT_APPLY_SETFIELD_MISS = ##(15) /* Apply Set-Field for table-miss. */
  val OFPTFPT_EXPERIMENTER        = ##(0xFFFE) /* Experimenter property. */
  val OFPTFPT_EXPERIMENTER_MISS   = ##(0xFFFF) /* Experimenter for table-miss. */
}

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_table_feature_prop_type._

object ofp_table_feature_props {
  type All =  union [ofp_table_feature_prop_instructions]        #or  // OFPTFPT_INSTRUCTIONS
                    [ofp_table_feature_prop_instructions_miss]   #or  // OFPTFPT_INSTRUCTIONS_MISS
                    [ofp_table_feature_prop_next_tables]         #or  // OFPTFPT_NEXT_TABLES
                    [ofp_table_feature_prop_next_tables_miss]    #or  // OFPTFPT_NEXT_TABLES_MISS
                    [ofp_table_feature_prop_write_actions]       #or  // OFPTFPT_WRITE_ACTIONS
                    [ofp_table_feature_prop_write_actions_miss]  #or  // OFPTFPT_WRITE_ACTIONS_MISS
                    [ofp_table_feature_prop_apply_actions]       #or  // OFPTFPT_APPLY_ACTIONS
                    [ofp_table_feature_prop_apply_actions_miss]  #or  // OFPTFPT_APPLY_ACTIONS_MISS
                    [ofp_table_feature_prop_match]               #or  // OFPTFPT_MATCH
                    [ofp_table_feature_prop_wildcards]           #or  // OFPTFPT_WILDCARDS
                    [ofp_table_feature_prop_write_setfield]      #or  // OFPTFPT_WRITE_SETFIELD
                    [ofp_table_feature_prop_write_setfield_miss] #or  // OFPTFPT_WRITE_SETFIELD_MISS
                    [ofp_table_feature_prop_apply_setfield]      #or  // OFPTFPT_APPLY_SETFIELD
                    [ofp_table_feature_prop_apply_setfield_miss] #or  // OFPTFPT_APPLY_SETFIELD_MISS
                    [ofp_table_feature_prop_experimenter]        #or  // OFPTFPT_EXPERIMENTER
                    [ofp_table_feature_prop_experimenter_miss]        // OFPTFPT_EXPERIMENTER_MISS
}

trait ofp_table_feature_prop extends WordTaggedUnion[ofp_table_feature_props.All, OFP_TABLE_FEATURE_PROP_TYPE] {
  def len: UInt16
}

private [structures] trait TFPHelper extends RawSeqFieldsInfo {
  protected val staticSize: Short = 4
  protected val staticFieldsCount = 1

  protected def len(is: RawSeq[_], elemSize: Int = 4): UInt16 =
    UInt16.fromShort((staticSize + elemSize * is.length).toShort)

  val rawFieldsLengthCalculator: LengthCalculator = {
    case n if n == staticFieldsCount    => Some { s => lenField(1)(s) - staticSize }
    case n if n == staticFieldsCount + 1=> Some { s => /* (lenField(1)(s) + 7) / 8 * 8 */ 0} // TODO: fix calculation
  }
}

case class ofp_table_feature_prop_instructions private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
   * - Exactly (length - 4) bytes containing the instruction ids, then
   * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
   * bytes of all-zero bytes */
  instructions: RawSeq[ofp_instruction_id],/* List of instructions */
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_instructions extends TFPHelper{
  private [protocol] def build(instruction_ids: RawSeq[ofp_instruction_id]) =
    ofp_table_feature_prop_instructions(len(instruction_ids), instruction_ids, RawSeq())

  def apply(instruction_ids: Array[ofp_instruction_id]): ofp_table_feature_prop_instructions = build(RawSeq(instruction_ids: _*))
}

case class ofp_table_feature_prop_instructions_miss private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  instructions: RawSeq[ofp_instruction_id],/* List of instructions */
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_instructions_miss extends TFPHelper{
  private [protocol] def build(instruction_ids: RawSeq[ofp_instruction_id]) =
    ofp_table_feature_prop_instructions_miss(len(instruction_ids), instruction_ids, RawSeq())

  def apply(instruction_ids: Array[ofp_instruction_id]): ofp_table_feature_prop_instructions_miss = build(RawSeq(instruction_ids: _*))
}

case class ofp_table_feature_prop_next_tables private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  next_table_ids: RawSeq[UInt8],/* List of instructions */
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_next_tables extends TFPHelper{
  private [protocol] def build(next_table_ids: RawSeq[UInt8]) =
    ofp_table_feature_prop_next_tables(len(next_table_ids, 1), next_table_ids, RawSeq())

  def apply(next_table_ids: Array[UInt8]): ofp_table_feature_prop_next_tables = build(RawSeq(next_table_ids: _*))
}

case class ofp_table_feature_prop_next_tables_miss private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  next_table_ids: RawSeq[UInt8],/* List of instructions */
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_next_tables_miss extends TFPHelper{
  private [protocol] def build(next_table_ids: RawSeq[UInt8]) =
    ofp_table_feature_prop_next_tables_miss(len(next_table_ids, 1), next_table_ids, RawSeq())

  def apply(next_table_ids: Array[UInt8]): ofp_table_feature_prop_next_tables_miss = build(RawSeq(next_table_ids: _*))
}

case class ofp_table_feature_prop_write_actions private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  action_ids: RawSeq[ofp_action_id],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_write_actions extends TFPHelper{
  private [protocol] def build(action_ids: RawSeq[ofp_action_id]) =
    ofp_table_feature_prop_write_actions(len(action_ids), action_ids, RawSeq())

  def apply(action_ids: Array[ofp_action_id]): ofp_table_feature_prop_write_actions = build(RawSeq(action_ids: _*))
}

case class ofp_table_feature_prop_write_actions_miss private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  action_ids: RawSeq[ofp_action_id] ,
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_write_actions_miss extends TFPHelper{
  private [protocol] def build(action_ids: RawSeq[ofp_action_id]) =
    ofp_table_feature_prop_write_actions_miss(len(action_ids), action_ids, RawSeq())

  def apply(action_ids: Array[ofp_action_id]): ofp_table_feature_prop_write_actions_miss = build(RawSeq(action_ids: _*))
}

case class ofp_table_feature_prop_apply_actions private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  action_ids: RawSeq[ofp_action_id],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_apply_actions extends TFPHelper{
  private [protocol] def build(action_ids: RawSeq[ofp_action_id]) =
    ofp_table_feature_prop_apply_actions(len(action_ids), action_ids, RawSeq())

  def apply(action_ids: Array[ofp_action_id]): ofp_table_feature_prop_apply_actions = build(RawSeq(action_ids: _*))
}

case class ofp_table_feature_prop_apply_actions_miss private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  action_ids: RawSeq[ofp_action_id],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_apply_actions_miss extends TFPHelper{
  private [protocol] def build(action_ids: RawSeq[ofp_action_id]) =
    ofp_table_feature_prop_apply_actions_miss(len(action_ids), action_ids, RawSeq())

  def apply(action_ids: Array[ofp_action_id]): ofp_table_feature_prop_apply_actions_miss = build(RawSeq(action_ids: _*))
}

case class ofp_table_feature_prop_experimenter private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  experimenter: UInt32, /* Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
  exp_type: UInt32, /* Experimenter defined. */
  /* Followed by:
  * - Exactly (length - 12) bytes containing the experimenter data, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  experimenter_data: RawSeq[UInt8],/* List of instructions */
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_experimenter extends TFPHelper{
  protected override val staticSize: Short = 12
  protected override val staticFieldsCount = 3

  private [protocol] def build(experimenter_data: RawSeq[UInt8], experimenter: UInt32, exp_type: UInt32) =
    ofp_table_feature_prop_experimenter(len(experimenter_data, 1), experimenter, exp_type, experimenter_data, RawSeq())

  def apply(experimenter_data: Array[UInt8], experimenter: UInt32, exp_type: UInt32): ofp_table_feature_prop_experimenter =
    build(RawSeq(experimenter_data: _*), experimenter, exp_type)
}

case class ofp_table_feature_prop_experimenter_miss private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  experimenter: UInt32, /* Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
  exp_type: UInt32, /* Experimenter defined. */
  /* Followed by:
  * - Exactly (length - 12) bytes containing the experimenter data, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  experimenter_data: RawSeq[UInt8],/* List of instructions */
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_experimenter_miss extends TFPHelper{
  protected override val staticSize: Short = 12
  protected override val staticFieldsCount = 3

  private [protocol] def build(experimenter_data: RawSeq[UInt8], experimenter: UInt32, exp_type: UInt32) =
    ofp_table_feature_prop_experimenter_miss(len(experimenter_data, 1), experimenter, exp_type, experimenter_data, RawSeq())

  def apply(experimenter_data: Array[UInt8], experimenter: UInt32, exp_type: UInt32): ofp_table_feature_prop_experimenter_miss =
    build(RawSeq(experimenter_data: _*), experimenter, exp_type)
}

case class ofp_table_feature_prop_match private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  oxm_ids: RawSeq[UInt32],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_match extends TFPHelper{
  private [protocol] def build(oxm_ids: RawSeq[UInt32]) =
    ofp_table_feature_prop_match(len(oxm_ids), oxm_ids, RawSeq())

  def apply(oxm_ids: Array[UInt32]): ofp_table_feature_prop_match = build(RawSeq(oxm_ids: _*))
}

case class ofp_table_feature_prop_wildcards private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  oxm_ids: RawSeq[UInt32],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_wildcards extends TFPHelper{
  private [protocol] def build(oxm_ids: RawSeq[UInt32]) =
    ofp_table_feature_prop_wildcards(len(oxm_ids), oxm_ids, RawSeq())

  def apply(oxm_ids: Array[UInt32]): ofp_table_feature_prop_wildcards = build(RawSeq(oxm_ids: _*))
}

case class ofp_table_feature_prop_write_setfield private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  oxm_ids: RawSeq[UInt32],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_write_setfield extends TFPHelper{
  private [protocol] def build(oxm_ids: RawSeq[UInt32]) =
    ofp_table_feature_prop_write_setfield(len(oxm_ids), oxm_ids, RawSeq())

  def apply(oxm_ids: Array[UInt32]): ofp_table_feature_prop_write_setfield = build(RawSeq(oxm_ids: _*))
}

case class ofp_table_feature_prop_write_setfield_miss private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  oxm_ids: RawSeq[UInt32],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_write_setfield_miss extends TFPHelper{
  private [protocol] def build(oxm_ids: RawSeq[UInt32]) =
    ofp_table_feature_prop_write_setfield_miss(len(oxm_ids), oxm_ids, RawSeq())

  def apply(oxm_ids: Array[UInt32]): ofp_table_feature_prop_write_setfield_miss = build(RawSeq(oxm_ids: _*))
}

case class ofp_table_feature_prop_apply_setfield private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  oxm_ids: RawSeq[UInt32],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_apply_setfield extends TFPHelper{
  private [protocol] def build(oxm_ids: RawSeq[UInt32]) =
    ofp_table_feature_prop_apply_setfield(len(oxm_ids), oxm_ids, RawSeq())

  def apply(oxm_ids: Array[UInt32]): ofp_table_feature_prop_apply_setfield = build(RawSeq(oxm_ids: _*))
}

case class ofp_table_feature_prop_apply_setfield_miss private [protocol] (
  len: UInt16,/* Length in bytes of this property. */
  /* Followed by:
  * - Exactly (length - 4) bytes containing the instruction ids, then
  * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
  * bytes of all-zero bytes */
  oxm_ids: RawSeq[UInt32],
  pad: RawSeq[UInt8]) extends ofp_table_feature_prop

object ofp_table_feature_prop_apply_setfield_miss extends TFPHelper{
  private [protocol] def build(oxm_ids: RawSeq[UInt32]) =
    ofp_table_feature_prop_apply_setfield_miss(len(oxm_ids), oxm_ids, RawSeq())

  def apply(oxm_ids: Array[UInt32]): ofp_table_feature_prop_apply_setfield_miss = build(RawSeq(oxm_ids: _*))
}
