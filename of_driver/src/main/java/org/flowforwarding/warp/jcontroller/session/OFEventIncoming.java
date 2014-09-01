/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.jcontroller.session;

import org.flowforwarding.warp.ofswitch.SwitchState.SwitchRef;

/**
 * @author Infoblox Inc.
 *
 */
public class OFEventIncoming extends OFEvent {

   protected SwitchRef switchRef;
   
   /**
    * @param switchRef
    * Reference to Switch
    */
   public OFEventIncoming(SwitchRef switchRef) {
      this.switchRef = switchRef;
   }

   /**
    * @return switchRef
    * @see OFEvent#getSwitchRef()
    */
   @Override
   public SwitchRef getSwitchRef() {
      // TODO Auto-generated method stub
      return switchRef;
   }

}
