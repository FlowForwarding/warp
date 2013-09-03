/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.supply;

import org.flowforwarding.of.controller.session.Event;
import akka.actor.ActorRef;

/**
 * @author Infoblox Inc.
 *
 */
// TODO Improvs. Replace ActorRef and Actor with a wrapper class for better decoupling
public class OFCTellController extends OFCTell<ActorRef, ActorRef, Event>{
   
   public OFCTellController(ActorRef controller) {
      sender = controller;
   }
   
   public void tell (ActorRef handler, Event event) {
      handler.tell(event, sender);
   }
}
