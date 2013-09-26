/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageError.OFMessageErrorRef;

/**
 * @author Infoblox Inc.
 * @doc.desc OpenFlow Error event 
 */
public class OFEventError extends OFEvent{

   protected SwitchRef switchRef;
   protected OFMessageErrorRef error;
   
   /**
    * @param SwitchRef switchRef
    * Switch issued an OpenFlow Error message
    * @param OFMessageErrorRef ofErrorRef
    * OpenFlow Error message itself
    */
   public OFEventError (SwitchRef switchRef, OFMessageErrorRef ofErrorRef) {
      this.switchRef = switchRef;
      error = ofErrorRef;
   }
   
   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.session.Event#getSwitchRef()
    */
   /**
    * @return SwitchRef switchRef  
    */
   @Override
   public SwitchRef getSwitchRef() {
      return switchRef;
   }
   
   /**
    * @return OFMessageErrorRef error
    */
   public OFMessageErrorRef getError() {
      return error;
   }

   /**
    * @param error
    * - Reference to OpenFlow error message
    */
   public void setError(OFMessageErrorRef error) {
      this.error = error;
   }

}
