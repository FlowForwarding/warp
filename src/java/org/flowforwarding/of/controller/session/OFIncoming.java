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
public class OFIncoming implements OFEvent {

   protected SwitchRef switchRef;
   
   /**
    * @param switchRef
    */
   public OFIncoming(SwitchRef swRef) {
      switchRef = swRef;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.session.OFEvent#getSwitchRef()
    */
   @Override
   public SwitchRef getSwitchRef() {
      // TODO Auto-generated method stub
      return null;
   }

}
