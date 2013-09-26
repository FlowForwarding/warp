/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;

/**
 * @author Infoblox Inc.
 * @doc Interface for all types of Events 
 *
 */
public interface Event {
   
   //TODO Improv. Move this to OFEvent?
   public SwitchRef getSwitchRef ();

}
