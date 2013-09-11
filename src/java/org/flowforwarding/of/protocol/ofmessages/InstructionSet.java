/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction;
import org.flowforwarding.of.protocol.ofstructures.Tuple;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionHandler;

/**
 * @author Infoblox Inc.
 *
 */
public class InstructionSet {
   
   protected List<Tuple<String, OFStructureInstructionHandler>> instructionSet = new ArrayList<>();
   protected Iterator<Tuple<String, OFStructureInstructionHandler>> iter = instructionSet.iterator();
   
   // TODO Improvements: Is this ok solution to return the List? Think about Avro API and AKKA Api decoupling in general.
   // TODO Improvements: Use this class as a Wrapper around the List: implement next(), hasNext() etc.
   public List<Tuple<String, OFStructureInstructionHandler>> getInstructions() {
      return instructionSet;
   }
   
   public Iterator<Tuple<String, OFStructureInstructionHandler>> getIterator () {
      return iter;
   }
   
   public void add (String name, OFStructureInstructionHandler value) {
      instructionSet.add(new Tuple<String, OFStructureInstructionHandler>(name, value));
   }

}
