/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed
package v13
package messages.symmetric

import messages.async.{Error => msgError}
import spire.syntax.literals._

trait Ofp13ExperimenterTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    ExperimenterInput(ui"1", ui"1", Array(1,2,3,4,5,6,7,8).map(_.toByte)) -> TestNoError(classOf[msgError], "Experimenter") //TODO is it right?
  }
}


