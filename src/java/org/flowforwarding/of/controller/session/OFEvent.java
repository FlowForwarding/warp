package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;

public interface OFEvent {
   
   public SwitchRef getSwitchRef ();

}
