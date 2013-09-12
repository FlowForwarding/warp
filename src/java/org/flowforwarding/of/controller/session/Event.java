package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;

public interface Event {
   
   //TODO Improv. Move this to OFEvent?
   public SwitchHandler getSwitchHandler ();

}
