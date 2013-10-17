/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.warp.controller.session;

import org.flowforwarding.warp.ofswitch.SwitchState.SwitchRef;

/**
 * @author Infoblox Inc.
 * @doc.desc Interface for all types of Events 
 *
 */
public interface Event {
   
   //TODO Improv. Move this to OFEvent?
   public SwitchRef getSwitchRef ();

}
