/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.Union.union


import ofp_instruction_type._
import instruction_length._

object ofp_instruction_ids {
  type All = union [ofp_instruction_id_goto_table]     #or
                   [ofp_instruction_id_write_metadata] #or
                   [ofp_instruction_id_write_actions]  #or
                   [ofp_instruction_id_apply_actions]  #or
                   [ofp_instruction_id_clear_actions]  #or
                   [ofp_instruction_id_meter]          #or
                   [ofp_instruction_id_experimenter]
}

trait ofp_instruction_id extends WordTaggedUnion[ofp_instruction_ids.All, OFP_INSTRUCTION_TYPE]{
  def length: INSTRUCTION_LENGTH
}

case class ofp_instruction_id_goto_table private[protocol] (length: INSTRUCTION_LENGTH) extends ofp_instruction_id
object ofp_instruction_id_goto_table {
  private [protocol] def build() = ofp_instruction_id_goto_table(IL_GOTO_TABLE)
}

case class ofp_instruction_id_write_metadata private[protocol] (length: INSTRUCTION_LENGTH) extends ofp_instruction_id
object ofp_instruction_id_write_metadata {
  private [protocol] def build() = ofp_instruction_id_write_metadata(IL_WRITE_METADATA)
}


case class ofp_instruction_id_write_actions private[protocol] (length: INSTRUCTION_LENGTH) extends ofp_instruction_id
object ofp_instruction_id_write_actions {
  private [protocol] def build() = ofp_instruction_id_write_metadata(IL_ACTIONS)
}

case class ofp_instruction_id_apply_actions private[protocol] (length: INSTRUCTION_LENGTH) extends ofp_instruction_id
object ofp_instruction_id_apply_actions {
  private [protocol] def build() = ofp_instruction_id_apply_actions(IL_ACTIONS)
}

case class ofp_instruction_id_clear_actions private[protocol] (length: INSTRUCTION_LENGTH) extends ofp_instruction_id
object ofp_instruction_id_clear_actions {
  private [protocol] def build() = ofp_instruction_id_clear_actions(IL_ACTIONS)
}

case class ofp_instruction_id_meter private[protocol] (length: INSTRUCTION_LENGTH) extends ofp_instruction_id
object ofp_instruction_id_meter {
  private [protocol] def build() = ofp_instruction_id_meter(IL_METER)
}

case class ofp_instruction_id_experimenter private[protocol] (length: INSTRUCTION_LENGTH) extends ofp_instruction_id
object ofp_instruction_id_experimenter {
  private [protocol] def build() = ofp_instruction_id_experimenter(IL_EXPERIMENTER)
}