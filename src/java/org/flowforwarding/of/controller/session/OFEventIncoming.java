/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;

/**
 * @author Infoblox Inc.
 *
 */
public class OFEventIncoming extends OFEvent {

   protected SwitchRef switchRef;
   
   /**
    * @param SwitchRef switchRef
    */
   public OFEventIncoming(SwitchRef swR) {
      switchRef = swR;
   }

   /**
    * @return SwitchRef switchRef
    * @see org.flowforwarding.of.controller.session.OFEvent#getSwitchRef()
    */
   @Override
   public SwitchRef getSwitchRef() {
      // TODO Auto-generated method stub
      return switchRef;
   }

}
