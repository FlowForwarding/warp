/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import akka.actor.ActorPath;
import akka.actor.ActorRef;

/**
 * @author Infoblox Inc.
 * @doc OpenFlow reference to Akka actor class 
 *
 */
public class OFActorRef extends ActorRef{

   /* (non-Javadoc)
    * @see akka.actor.ActorRef#isTerminated()
    */
   @Override
   public boolean isTerminated() {
      return false;
   }

   /* (non-Javadoc)
    * @see akka.actor.ActorRef#path()
    */
   @Override
   public ActorPath path() {
      // TODO Auto-generated method stub
      return null;
   }

}
