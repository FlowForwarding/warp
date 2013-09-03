package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;

public interface Event {
   
   //TODO Improv. Move this to OFEvent?
   public SwitchRef getSwitchRef ();

}
