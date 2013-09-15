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
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionHandler;
import org.flowforwarding.of.protocol.ofstructures.Tuple;
import org.flowforwarding.of.protocol.supply.OFMAddField;
import org.flowforwarding.of.protocol.supply.OFMAddInstruction;
import org.flowforwarding.of.protocol.supply.OFMAddMatch;
import org.flowforwarding.of.protocol.supply.OFMGetInstructions;
import org.flowforwarding.of.protocol.supply.OFMGetMatches;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageFlowMod extends OFMessage{

   protected List<Tuple<String, String>> parms = new ArrayList<>();
   protected Iterator<Tuple<String, String>> iter = parms.iterator();
   protected InstructionSet instructions;
   protected MatchSet matches;
   
   protected OFMessageFlowMod() {
      // TODO Improvs: create() instead of Constructor?
      matches = new MatchSet();
      instructions = new InstructionSet();
   }
   
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

   public void addInstruction (String name, OFStructureInstructionHandler value) {
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
   
   public static class OFMessageFlowModHandler extends OFMessageHandler <OFMessageFlowMod> {
      
      protected OFMessageFlowMod flowMod = null;
      
      protected OFMAddInstruction addInstruction = null;
      protected OFMAddMatch addMatch = null;
      protected OFMAddField addField = null;
      protected OFMGetInstructions getInstructions = null;
      protected OFMGetMatches getMatches = null;
      
      protected OFMessageFlowModHandler () {
         flowMod = new OFMessageFlowMod();
         
         addInstruction = new OFMAddInstruction(flowMod);
         addMatch = new OFMAddMatch(flowMod);
         addField = new OFMAddField(flowMod);
         getInstructions = new OFMGetInstructions(flowMod);
         getMatches = new OFMGetMatches(flowMod);
      }
      
      protected OFMessageFlowModHandler (OFMessageFlowMod fm) {
         flowMod = fm;
         
         addInstruction = new OFMAddInstruction(flowMod);
         addMatch = new OFMAddMatch(flowMod);
         addField = new OFMAddField(flowMod);
         getInstructions = new OFMGetInstructions(flowMod);
         getMatches = new OFMGetMatches(flowMod);
      }
      
      public static OFMessageFlowModHandler create() {
         
         return new OFMessageFlowModHandler();
      }
      
      public void addField (String name, String value) {
         addField.add(name, value);
      }
      
      public void addInstruction (String name, OFStructureInstructionHandler instruction) {
         addInstruction.add(name, instruction);
      }
      
      public void addInstructionApplyAction (OFStructureInstructionHandler instruction) {
         addInstruction.add("apply_action", instruction);
      }
      
      public void addInstructionWriteAction (OFStructureInstructionHandler instruction) {
         addInstruction.add("write_action", instruction);
      }
      
      public void addInstructionClearAction (OFStructureInstructionHandler instruction) {
         addInstruction.add("clear_action", instruction);
      }

      
      public void addMatch (String name, String match) {
         addMatch.add(name, match);
      }
      
      public void addMatchInPort(String value) {
         addMatch.add("ingress_port", value);
      }
      
      public void addMatchEthDst(String value) {
         addMatch.add("eth_dst", value);
      }
      
      public void addMatchEthSrc(String value) {
         addMatch.add("eth_src", value);
      }
      
      public InstructionSet getInstructions() {
         return getInstructions.get();
      }
      
      public MatchSet getMatches() {
         return getMatches.get();
      }
      
   }
}
