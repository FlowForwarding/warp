/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.flowforwarding.of.protocol.ofstructures.MatchSet;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction;
import org.flowforwarding.of.protocol.ofstructures.Tuple;
import org.flowforwarding.of.protocol.supply.OFMAddField;
import org.flowforwarding.of.protocol.supply.OFMAddInstruction;
import org.flowforwarding.of.protocol.supply.OFMAddMatch;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageFlowMod extends OFMessage{

   protected List<Tuple<String, String>> parms = new ArrayList<>();
   protected Iterator<Tuple<String, String>> iter = parms.iterator();
   protected InstructionSet instructions;
   protected MatchSet matches;
   
   public List<Tuple<String, String>> getParms() {
      return parms;
   }

   public void setParms(List<Tuple<String, String>> parms) {
      this.parms = parms;
   }

   public InstructionSet getInstructions() {
      return instructions;
   }

   public void setInstructions(InstructionSet instructions) {
      this.instructions = instructions;
   }

   public Iterator<Tuple<String, String>> getIterator () {
      return iter;
   }
   
   public void add (String name, String value) {
      parms.add(new Tuple<String, String>(name, value));
   }

   public void addInstruction (String name, OFStructureInstruction value) {
      instructions.add(name, value);
   }
   
   public void setMatches(MatchSet matches) {
      this.matches = matches;
   }

   // TODO Improvements: Is this ok solution to return the List? Think about Avro API and AKKA Api decoupling in general.
   // TODO Improvements: Use this class as a Wrapper around the List: implement next(), hasNext() etc.
   public MatchSet getMatches() {
      return matches;
   }

   public void addMatch (String name, String value) {
      matches.add(name, value);
   }
   
   public static class OFMessageFlowModeRef extends OFMessageRef <OFMessageFlowMod> {
      
      protected OFMessageFlowMod flowMod = null;
      
      protected OFMAddInstruction addInstruction = null;
      protected OFMAddMatch addMatch = null;
      protected OFMAddField addField = null;
      
      protected OFMessageFlowModeRef () {
         flowMod = new OFMessageFlowMod();
         
         addInstruction = new OFMAddInstruction(flowMod);
         addMatch = new OFMAddMatch(flowMod);
         addField = new OFMAddField(flowMod);
      }
      
      protected OFMessageFlowModeRef (OFMessageFlowMod fm) {
         flowMod = fm;
         
         addInstruction = new OFMAddInstruction(flowMod);
         addMatch = new OFMAddMatch(flowMod);
         addField = new OFMAddField(flowMod);
      }
      
      public static OFMessageFlowModeRef create() {
         
         return new OFMessageFlowModeRef();
      }
      
      public void addField (String name, String value) {
         addField.add(name, value);
      }
      
      public void addInstruction (String name, OFStructureInstruction instruction) {
         addInstruction.add(name, instruction);
      }
      
      public void addMatch (String name, String match) {
         addMatch.add(name, match);
      }
      
   }
}
