/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.flowforwarding.warp.util.Tuple;

/**
 * @author Infoblox Inc.
 *
 */
public class ActionSet {
   
   protected List<Tuple<String, String>> actionSet = new ArrayList<>();
   protected Iterator<Tuple<String, String>> iter = actionSet.iterator();
   
   // TODO Improvements: Is this ok solution to return the List? Think about Avro API and AKKA Api decoupling in general.
   // TODO Improvements: Use this class as a Wrapper around the List: implement next(), hasNext() etc.
   public List<Tuple<String, String>> getActions() {
      return actionSet;
   }
   
   public Iterator<Tuple<String, String>> getIterator () {
      return iter;
   }
   
   public void add (String name, String value) {
      actionSet.add(new Tuple<String, String>(name, value));
   }

}
