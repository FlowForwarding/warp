/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod

import spire.syntax.literals._
import spire.math.UShort

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util.IPv4Address
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.{NoBuffer, Max, ControllerMaxLength}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.async.{Error => msgError}
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13DriverApi
import org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.instructions._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.actions.Action

trait Ofp13FlowModTest extends MessageTestsSet[Ofp13DriverApi]{
  def inputTemplate(instructions: Instruction*) = FlowModInput(
    ul"1",
    ul"0",
    uh"0",
    FlowModCommand.DeleteStrict,
    ui"1",
    ui"5",
    ui"100",
    ControllerMaxLength(uh"30"),
    PortNumber(ui"333"),
    GroupId(ui"7"),
    FlowModFlags(false, false, true, false, true),
    MatchInput(true, Array()),
    instructions.toArray
  )

  def oxmInputTemplate(oxm: OxmTlv[_]*) = FlowModInput(
    ul"0",
    ul"0",
    uh"0",
    FlowModCommand.Add,
    ui"0",
    ui"0",
    ui"500",
    NoBuffer,
    PortNumber.AllPorts,
    GroupId.AllGroups,
    FlowModFlags(false, false, true, false, true),
    MatchInput(true, oxm.toArray),
    Array(InstructionApplyActions(Array(Action.output(PortNumber(ui"2")))))
  )

  private def noErrorTestTemplate(description: String, instructions: Instruction*): (FlowModInput, TestData) =
    inputTemplate(instructions: _*) -> TestNoError(classOf[msgError], description)

  /* errorResponse* tests were broken after introduction of oxm_tlv test */
  private def errorResponseTestTemplate(description: String, eType: UShort, eCode: UShort, instructions: Instruction*): (FlowModInput, TestData) =
    inputTemplate(instructions: _*) -> TestResponse({ case e: msgError => e.errorType == eType && e.errorCode == eCode }, description)

  abstract override def tests = super.tests +
    noErrorTestTemplate("Flow mod: empty") +
    noErrorTestTemplate("Flow mod: InstructionGotoTable", InstructionGotoTable(ub"10")) +
    noErrorTestTemplate("Flow mod: InstructionGotoTable", InstructionWriteMetadata(ul"10", ul"10")) +
    noErrorTestTemplate("Flow mod: InstructionGotoTable", InstructionMeter(ui"100")) +
    //errorResponseTestTemplate("Flow mod: InstructionGotoTable", eType = uh"3", eCode = uh"1", InstructionMeter(ui"100")) + /* Unsupported instruction */
    noErrorTestTemplate("Flow mod: InstructionGotoTable", InstructionWriteMetadata(ul"20", ul"20"), InstructionGotoTable(ub"1")) +
/*  InstructionExperimenter causes a crash of LINC-Switch. Should be tested separately from other messages.  */
//  noErrorTestTemplate("Flow mod: InstructionExperimenter", InstructionExperimenter(500)) +
//  noErrorTestTemplate("Flow mod: InstructionExperimenter with data", InstructionExperimenter(100, Array[Byte](1, 2, 3, 4, 5)))
    noErrorTestTemplate("Flow mod: InstructionClearActions",  InstructionClearActions()) +
    noErrorTestTemplate("Flow mod: ActionSetQueue (Apply)",   InstructionApplyActions(Array(Action.setQueue(ui"1")))) +
    noErrorTestTemplate("Flow mod: ActionSetMplsTtl (Apply)", InstructionApplyActions(Array(Action.setMplsTtl(ub"1")))) +
    noErrorTestTemplate("Flow mod: ActionSetNwTtl",           InstructionWriteActions(Array(Action.setNwTtl(ub"1")))) +
    noErrorTestTemplate("Flow mod: ActionPopVlan",            InstructionWriteActions(Array(Action.popVlan))) +
    noErrorTestTemplate("Flow mod: ActionPopMpls",            InstructionWriteActions(Array(Action.popMpls(uh"1")))) +
    noErrorTestTemplate("Flow mod: Array of actions",         InstructionWriteActions(Array(Action.setQueue(ui"1"), Action.setMplsTtl(ub"1"), Action.setNwTtl(ub"1")))) +
//  errorResponseTestTemplate("Flow mod: ActionGroup",    eType = uh"2", eCode = uh"9", InstructionWriteActions(Array(Action.group(GroupId(ui"10"))))) +       /* BAD_OUT_GROUP */
//  errorResponseTestTemplate("Flow mod: ActionPushVlan", eType = uh"2", eCode = uh"5", InstructionWriteActions(Array(Action.pushVlan(uh"1")))) +  /* BAD_ARGUMENT */
//  errorResponseTestTemplate("Flow mod: ActionPushMpls", eType = uh"2", eCode = uh"5", InstructionWriteActions(Array(Action.pushMpls(uh"1")))) +  /* BAD_ARGUMENT */
//  errorResponseTestTemplate("Flow mod: ActionOutput",   eType = uh"2", eCode = uh"4", InstructionWriteActions(Array(Action.output(PortNumber(ui"100"), Max)))) + /* BAD_OUT_PORT */
    /* Tests for OxmTlv */
    (oxmInputTemplate(in_port(ui"2"))                                                     -> TestNoError(classOf[msgError], "Flow mod: XML TLV in_port")) +
    (oxmInputTemplate(in_port(ui"1"), eth_type(uh"2048"), vlan_vid(uh"4", Some(uh"255"))) -> TestNoError(classOf[msgError], "Flow mod: bunch of oxm tlvs")) +
    noErrorTestTemplate("Flow mod: Array of actions",         InstructionWriteActions(Array(Action.setQueue(ui"1"), Action.setMplsTtl(ub"1"), Action.setNwTtl(ub"1")))) +
    noErrorTestTemplate("Flow mod: ActionGroup",              InstructionWriteActions(Array(Action.group(GroupId(ui"10"))))) +
    noErrorTestTemplate("Flow mod: ActionPushVlan",           InstructionWriteActions(Array(Action.pushVlan(uh"1")))) +
    noErrorTestTemplate("Flow mod: ActionPushMpls",           InstructionWriteActions(Array(Action.pushMpls(uh"1")))) +
    noErrorTestTemplate("Flow mod: ActionOutput",             InstructionWriteActions(Array(Action.output(PortNumber(ui"100"), Max))))
    /* These actions cause crashes of LINC-Switch. Should be tested separately from other messages.  */
    //noErrorTestTemplate("Flow mod: ActionPushPbb",      InstructionWriteActions(Array(Action.pushPbb(1)))) +
    //noErrorTestTemplate("Flow mod: ActionPopPbb",       InstructionWriteActions(Array(Action.popPbb))) +
    //noErrorTestTemplate("Flow mod: ActionCopyTtlOut",   InstructionWriteActions(Array(Action.copyTtlOut))) +
    //noErrorTestTemplate("Flow mod: ActionCopyTtlIn",    InstructionWriteActions(Array(Action.copyTtlIn))) +
    //noErrorTestTemplate("Flow mod: ActionExperimenter", InstructionWriteActions(Array(Action.experimenter(10))))
}