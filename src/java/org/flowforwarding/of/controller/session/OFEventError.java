/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageError.OFMessageErrorHandler;

/**
 * @author Infoblox Inc.
 *
 */
public class OFEventError extends OFEvent{

   protected SwitchHandler switchH;
   protected OFMessageErrorHandler error;
   
   public OFEventError (SwitchHandler swH, OFMessageErrorHandler er) {
      switchH = swH;
      error = er;
   }
   
   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.session.Event#getSwitchHandler()
    */
   @Override
   public SwitchHandler getSwitchHandler() {
      return switchH;
   }
   
   /**
    * @return the error
    */
   public OFMessageErrorHandler getError() {
      return error;
   }

   /**
    * @param error the error to set
    */
   public void setError(OFMessageErrorHandler error) {
      this.error = error;
   }

}
