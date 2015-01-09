/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller.mod

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures._

import spire.syntax.literals._
import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.Bucket
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.actions.Action

trait Ofp13GroupModTest extends MessageTestsSet[Ofp13DriverApi] {

  abstract override def tests = super.tests +
    (GroupModInput(GroupModCommand.Add, GroupModType.All, GroupId(ui"1"), Array(Bucket.build(uh"1", PortNumber(ui"2"), GroupId(ui"1"), Array.empty)))
      -> TestNoError("Add Group (no actions). NOTE: This test may fail if it was executed in wrong order" )) +
    (GroupModInput(GroupModCommand.Modify, GroupModType.All, GroupId(ui"1"), Array(Bucket.build(uh"1", PortNumber(ui"2"), GroupId(ui"1"), Array(Action.decMplsTtl, Action.decNwTtl))))
      -> TestNoError("Modify Group (with actions). NOTE: This test may fail if it was executed in wrong order" )) +
    (GroupModInput(GroupModCommand.Delete, GroupModType.All, GroupId(ui"1"), Array.empty)
      -> TestNoError( "Delete Group (no buckets). NOTE: This test may fail if it was executed in wrong order" ))
//    (GroupModInput(GroupModCommand.Add,GroupModType.All, GroupId(1), Array.empty) -> TestNoError(classOf[messages.async.Error], "GroupMod (no buckets)" ))
//    (GroupModInput(GroupModCommand.Add,GroupModType.All, GroupId(1), Array.empty) -> TestNoError(classOf[messages.async.Error], "GroupMod (no buckets)" ))
}