package org.flowforwarding.of.controller.protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Array;
import org.apache.avro.generic.GenericData.Fixed;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.GenericData.EnumSymbol;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificFixed;
import org.apache.avro.Protocol;
import org.javatuples.Pair;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.instruction.OFInstructionApplyActions;
import org.openflow.protocol.match.OFMatchType.*;
import org.openflow.util.U16;
import org.openflow.util.U8;

public class MessageProvider {
   
   //private final String schemaSrc = "src/resources/of_protocol_12p.avpr";
   //private final String schemaSrc = "of_protocol_12p.avpr";
   private final String schemaSrc = "of_protocol_131.avpr";
   //private final String schemaSrc = "test.avpr";
   
   private Schema ofpHeaderSchema = null;
   
   private Schema ofpHelloHeaderSchema = null;
   private Schema ofpHelloSchema = null;
   
   private Schema ofpSwitchFeaturesRequestSchema = null;
   private Schema ofpSwitchFeaturesRequestHeaderSchema = null;
   
   private Schema ofpSwitchFeaturesReplySchema = null;
   private Schema ofpSwitchFeaturesReplyHeaderSchema = null;
   
   private Schema ofpSwitchFeaturesSchema = null;
   private Schema ofpSwitchFeaturesHeaderSchema = null;
   
   private Schema ofpSwitchConfigSchema = null; 
   
   private Schema ofpSetSwitchConfigSchema;
   private Schema ofpSetSwitchConfigHeaderSchema;
   
   private Schema ofpGetConfigRequestHeaderSchema = null;
   private Schema ofpGetConfigRequestSchema = null;
   private Schema ofpGetConfigReplyHeaderSchema = null;
   private Schema ofpGetConfigReplySchema = null;
   
   private Schema ofpTypeSchema = null;
   private Schema ofpConfigFlagsSchema = null;   
   private Schema ofpFlowModCommandSchema = null;
   private Schema ofpFlowModFlagsSchema = null;
   
   private Schema uint_8Schema = null;
   private Schema uint_16Schema = null;
   private Schema uint_24Schema = null;
   private Schema uint_32Schema = null;
   private Schema uint_48Schema = null;
   private Schema uint_64Schema = null;
   private Schema uint_128Schema = null;
   
   /*
    * Delayed matches 
    */
   // TODO Implement a wrapper around matches.add to handle PREREQs
   private GenericRecord delayedVlanPcp = null; 
   private boolean isVlanVid = false;
   
   private GenericRecord delayedIpv4Src = null;
   private GenericRecord delayedIpv4Dst = null;
   private boolean isEtherType = false;

   
   private Protocol protocol = null;
   
   
   final class MatchEntry<K, V> implements Map.Entry<K, V> {
       private final K key;
       private V value;

       public MatchEntry(K key, V value) {
           this.key = key;
           this.value = value;
       }

       @Override
       public K getKey() {
           return key;
       }

       @Override
       public V getValue() {
           return value;
       }

       @Override
       public V setValue(V value) {
           V old = this.value;
           this.value = value;
           return old;
       }
   }
   
   public void init () {
      try {
         //protocol = org.apache.avro.Protocol.parse(new File(schemaSrc));
         protocol = org.apache.avro.Protocol.parse(getClass().getClassLoader().getResourceAsStream(schemaSrc));
         } catch (IOException e) {
         // TODO Auto-generated catch block
           e.printStackTrace();
         }

      ofpHeaderSchema =  protocol.getType("of.ofp_header");
      
      ofpHelloHeaderSchema = protocol.getType("of.ofp_hello_header");
      ofpHelloSchema = protocol.getType("of.ofp_hello");
      
      ofpSwitchFeaturesRequestSchema =  protocol.getType("of.ofp_switch_features_request");
      ofpSwitchFeaturesRequestHeaderSchema =  protocol.getType("of.ofp_switch_features_request_header");
      
      ofpSwitchFeaturesReplySchema =  protocol.getType("of.ofp_switch_features_reply");
      ofpSwitchFeaturesReplyHeaderSchema =  protocol.getType("of.ofp_switch_features_reply_header");
      
      ofpSetSwitchConfigSchema = protocol.getType("of.ofp_set_switch_config");
      ofpSetSwitchConfigHeaderSchema = protocol.getType("of.ofp_set_switch_config_header");
      
      ofpGetConfigRequestHeaderSchema = protocol.getType("of.ofp_get_config_request_header");
      ofpGetConfigRequestSchema = protocol.getType("of.ofp_get_config_request");
      ofpGetConfigReplyHeaderSchema = protocol.getType("of.ofp_get_config_reply_header");
      
      ofpSwitchFeaturesSchema = protocol.getType("of.ofp_switch_features");
      ofpSwitchFeaturesHeaderSchema = protocol.getType("of.ofp_switch_features_header");
      
      uint_8Schema = protocol.getType("of.uint_8");
      uint_16Schema = protocol.getType("of.uint_16");
      uint_16Schema = protocol.getType("of.uint_24");
      uint_32Schema = protocol.getType("of.uint_32");
      uint_48Schema = protocol.getType("of.uint_48");
      uint_64Schema = protocol.getType("of.uint_64");
      uint_128Schema = protocol.getType("of.uint_128");

      
   }
   
   public ByteArrayOutputStream getHello(ByteArrayOutputStream out) {
      
      GenericRecord ofpHelloRecord = new GenericData.Record(ofpHelloSchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(ofpHelloHeaderSchema);

      GenericRecord ofpHelloHeaderRecord = builder.build();
      ofpHelloRecord.put("header", ofpHelloHeaderRecord);  
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpHelloSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(ofpHelloRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return out;
    }
   
   public ByteArrayOutputStream getSwitchFeaturesRequest(ByteArrayOutputStream out) {
      
      GenericRecord ofpSwitchFeaturesRequestRecord = new GenericData.Record(ofpSwitchFeaturesRequestSchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(ofpSwitchFeaturesRequestHeaderSchema);

      GenericRecord ofpSwitchFeaturesRequestHeaderRecord = builder.build();
      ofpSwitchFeaturesRequestRecord.put("header", ofpSwitchFeaturesRequestHeaderRecord);  
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpSwitchFeaturesRequestSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(ofpSwitchFeaturesRequestRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return out;
    }
   
   public ByteArrayOutputStream getSwitchFeaturesReply(ByteArrayOutputStream out) {
      
      GenericRecord ofpSwitchFeaturesReplyRecord = new GenericData.Record(ofpSwitchFeaturesReplySchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(ofpSwitchFeaturesReplyHeaderSchema);
      
      GenericRecord ofpSwitchFeaturesReplyHeaderRecord = builder.build();
      ofpSwitchFeaturesReplyRecord.put("header", ofpSwitchFeaturesReplyHeaderRecord);  
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpSwitchFeaturesReplySchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(ofpSwitchFeaturesReplyRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return out;
   }
   
   public ByteArrayOutputStream getSetSwitchConfig(ByteArrayOutputStream out) {
      
      GenericRecord ofpSetSwitchConfigRecord = new GenericData.Record(ofpSetSwitchConfigSchema);
      
      GenericRecordBuilder builder = new GenericRecordBuilder(ofpSetSwitchConfigHeaderSchema);
      GenericRecord ofpSetSwitchConfigHeaderRecord = builder.build();
      
      ofpSetSwitchConfigRecord.put("header", ofpSetSwitchConfigHeaderRecord);
      
      byte[] fl = {0,0};
      GenericData.Fixed flags = new GenericData.Fixed(ofpSetSwitchConfigSchema, fl);
      ofpSetSwitchConfigRecord.put("flags", flags);
      
      byte[] msl = {(byte)255, (byte)255};
      GenericData.Fixed miss_send_len = new GenericData.Fixed(ofpSetSwitchConfigSchema, msl);
      ofpSetSwitchConfigRecord.put("miss_send_len", miss_send_len);
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpSetSwitchConfigSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(ofpSetSwitchConfigRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return out;
   }
   
   public ByteArrayOutputStream getSwitchConfigRequest(ByteArrayOutputStream out) {
      
      GenericRecord ofpGetConfigRequestRecord = new GenericData.Record(ofpGetConfigRequestSchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(ofpGetConfigRequestHeaderSchema);
      
      GenericRecord ofpGetConfigRequestHeaderRecord = builder.build();
      ofpGetConfigRequestRecord.put("header", ofpGetConfigRequestHeaderRecord);  
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpGetConfigRequestSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(ofpGetConfigRequestRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return out;
   }
   
   public ByteArrayOutputStream getFlowMod (Map<String, Object> args, ByteArrayOutputStream out) {
      
      /*
       * Build FlowMod message
       */
      Schema ofpFlowModSchema = protocol.getType("of.ofp_flow_mod");
      GenericRecord ofpFlowModRecord = new GenericData.Record(ofpFlowModSchema);
      
      /*
       * Create FlowMod Body
       */
      Schema flowModBodySchema = protocol.getType("of.flow_mod_body");
      GenericRecordBuilder flowModBodyBuilder = new GenericRecordBuilder(flowModBodySchema);
      GenericRecord flowModBodyRecord = flowModBodyBuilder.build();
      GenericRecord actionSetRecord = null; // TODO Empty actions instead of null!
      List <GenericRecord> instructions = new ArrayList<>();
      List <GenericRecord> matches = new ArrayList<>();
      
      for (String key : args.keySet()) {
         if (args.get(key) == null)
            continue;
         
         /*
          * PRIORITY
          */
         if (key.equals("priority")) {
            short priority = U16.t(Integer.valueOf((String) args.get(key)));
            byte temp [] = {(byte)(priority >> 8), (byte)(priority) };
            
            GenericData.Fixed priorityFixed = new GenericData.Fixed(uint_16Schema, temp);
          
            flowModBodyRecord.put("priority", priorityFixed);
         /*
          * ACTIONS   
          */
         } else if (key.equals("actions")) {
            actionSetRecord = parseActionString((String) args.get(key));
            
            /*
             * Build Write Actions Instruction for Test
             */
            Schema instrWriteActionsHeaderSchema = protocol.getType("of.instruction_write_actions_header");
            GenericRecordBuilder instrHeaderBuilder = new GenericRecordBuilder(instrWriteActionsHeaderSchema);
            GenericRecord instrWriteActionsHeaderRecord = instrHeaderBuilder.build();
            
            /*
             * Create ofp_instruction_write_actions
             */
            Schema ofpInstrWriteActionsSchema = protocol.getType("ofp_instruction_write_actions");
            GenericRecord ofpInstrWriteActionsRecord = new GenericData.Record(ofpInstrWriteActionsSchema);
            ofpInstrWriteActionsRecord.put("header", instrWriteActionsHeaderRecord);
            ofpInstrWriteActionsRecord.put("actions", actionSetRecord);
            
            /*
             * Calculate ofp_instruction_write_actions length 
             */
            
            ByteArrayOutputStream instrOut = new ByteArrayOutputStream();
            DatumWriter<GenericRecord> instrWriter = new GenericDatumWriter<GenericRecord>(ofpInstrWriteActionsSchema);
            Encoder instrEncoder = EncoderFactory.get().binaryNonEncoder(instrOut, null);
           
            try {
               instrWriter.write(ofpInstrWriteActionsRecord, instrEncoder);
               instrEncoder.flush();
               
               Schema uint16Schema = protocol.getType("of.uint_16");
               
               byte len[] = {(byte)(instrOut.size() >> 8), (byte)(255 & instrOut.size())}; 
               GenericData.Fixed lenght = new GenericData.Fixed(uint16Schema, len);
               
               instrWriteActionsHeaderRecord.put("length", lenght);
               ofpInstrWriteActionsRecord.put("header", instrWriteActionsHeaderRecord);
            } catch (IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }

            Schema ofpInstrSchema = protocol.getType("of.ofp_instruction");
            GenericRecord ofpInstrRecord = new GenericData.Record(ofpInstrSchema);

            ofpInstrRecord.put("instruction", ofpInstrWriteActionsRecord);
            instructions.add(ofpInstrRecord);
         /*
          * MATCH - INGRESS PORT
          */
         } else if (key.equals("in_port")) {
            
            Schema oxmTlvIngressPortSchema = protocol.getType("of.oxm_tlv_ingress_port");
            GenericRecord oxmTlvIngressPortRecord = new GenericData.Record(oxmTlvIngressPortSchema);
            
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                               (OXMField.OFPXMT_OFB_IN_PORT.getValue() << 9) | 
                               (0 << 8) |
                               4;
               
            byte inPH [] = {(byte)(oxmTlvHeader >> 24), (byte)(oxmTlvHeader >> 16), (byte)(oxmTlvHeader >> 8), (byte)(oxmTlvHeader) };
            int inPort = Integer.valueOf((String) args.get(key));
            GenericData.Fixed inPortHeader = new GenericData.Fixed(uint_32Schema, inPH);
            
            oxmTlvIngressPortRecord.put("header", inPortHeader);
            oxmTlvIngressPortRecord.put("tlv", getUint32Fixed(inPort));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvIngressPortRecord);
            
            matches.add(oxmTlvRecord);
            /*
             * MATCH - IN_PHY PORT
             */
         } else if (key.equals("in_phy_port")) {
            Schema oxmTlvInPhyPortSchema = protocol.getType("of.oxm_tlv_in_phy_port");
            GenericRecord oxmTlvInPhyPortRecord = new GenericData.Record(oxmTlvInPhyPortSchema);
               
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                               (OXMField.OFPXMT_OFB_IN_PHY_PORT.getValue() << 9) | 
                               (0 << 8) |
                               4;
                  
            byte inPH [] = {(byte)(oxmTlvHeader >> 24), (byte)(oxmTlvHeader >> 16), (byte)(oxmTlvHeader >> 8), (byte)(oxmTlvHeader) };
            int inPort = Integer.valueOf((String) args.get(key));
            GenericData.Fixed inPortHeader = new GenericData.Fixed(uint_32Schema, inPH);
               
            oxmTlvInPhyPortRecord.put("header", inPortHeader);
            oxmTlvInPhyPortRecord.put("tlv", getUint32Fixed(inPort));
               
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvInPhyPortRecord);
               
            matches.add(oxmTlvRecord);
            
            /*
             * MATCH - METADATA
             */
         } else if (key.equals("metadata")) {
            Schema oxmTlvMetadataSchema = protocol.getType("of.oxm_tlv_metadata");
            GenericRecord oxmTlvMetadataRecord = new GenericData.Record(oxmTlvMetadataSchema);
               
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                               (OXMField.OFPXMT_OFB_METADATA.getValue() << 9) | 
                               (0 << 8) |
                               8;
                  
            byte h [] = {(byte)(oxmTlvHeader >> 24), (byte)(oxmTlvHeader >> 16), (byte)(oxmTlvHeader >> 8), (byte)(oxmTlvHeader) };
            long mdata = Integer.valueOf((String) args.get(key));
            GenericData.Fixed inPortHeader = new GenericData.Fixed(uint_64Schema, h);
               
            oxmTlvMetadataRecord.put("header", inPortHeader);
            oxmTlvMetadataRecord.put("tlv", getUint64Fixed(mdata));
               
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvMetadataRecord);
               
            matches.add(oxmTlvRecord);
            
           /* 
            * MATCH - ETH_SRC
            */
         } else if (key.equals("dl_src")) {
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                (OXMField.OFPXMT_OFB_ETH_SRC.getValue() << 9) | 
                (0 << 8) |
                6;
            
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_eth_src");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
            
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint48Fixed(get_mac_addr((String) args.get(key))));
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            matches.add(oxmTlvRecord);

          /* 
           * MATCH - ETH_DST
           */
      } else if (key.equals("dl_dst")) {
         int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
               (OXMField.OFPXMT_OFB_ETH_DST.getValue() << 9) | 
               (0 << 8) |
               6;
         
         Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_eth_dst");
         GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);

         oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
         oxmTlvFieldRecord.put("tlv", getUint48Fixed(get_mac_addr((String) args.get(key))));

         Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
         GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
         oxmTlvRecord.put("match", oxmTlvFieldRecord);
         
         matches.add(oxmTlvRecord);
          
         /* 
          * MATCH - ETH_TYPE
          */
         } else if (key.equals("dl_type")) {
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                  (OXMField.OFPXMT_OFB_ETH_TYPE.getValue() << 9) | 
                  (0 << 8) |
                  2;
            
            Schema oxmTlvEthTypeSchema = protocol.getType("of.oxm_tlv_eth_type");
            GenericRecord oxmTlvEthTypeRecord = new GenericData.Record(oxmTlvEthTypeSchema);

            byte ethTH [] = {(byte)(oxmTlvHeader >> 24), (byte)(oxmTlvHeader >> 16), (byte)(oxmTlvHeader >> 8), (byte)(oxmTlvHeader) };
            short ethType = U16.t(Integer.valueOf(((String) args.get(key)).replaceFirst("0x", ""), 16));
            //U16.t(Integer.valueOf((String) args.get(key)));
            GenericData.Fixed ethTypeHeader = new GenericData.Fixed(uint_32Schema, ethTH);
                        
            oxmTlvEthTypeRecord.put("header", ethTypeHeader);
            oxmTlvEthTypeRecord.put("tlv", getUint16Fixed(ethType));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvEthTypeRecord);
   
            matches.add(oxmTlvRecord);
            
            if (this.delayedIpv4Src != null)
               matches.add(this.delayedIpv4Src);
            if (this.delayedIpv4Dst != null)
               matches.add(this.delayedIpv4Dst);
            
            this.isEtherType = true;

        /*
         * VLAN_VID
         */
        } else if (key.equals("vlan_vid")) {
           
           Schema oxmTlvVlanVidSchema = protocol.getType("of.oxm_tlv_vlan_vid");
           GenericRecord oxmTlvVlanVidRecord = new GenericData.Record(oxmTlvVlanVidSchema);
           
           int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                              (OXMField.OFPXMT_OFB_VLAN_VID.getValue() << 9) | 
                              (0 << 8) |
                              2;
           short vid = U16.t(Integer.valueOf((String) args.get(key)));
           
           oxmTlvVlanVidRecord.put("header", getUint32Fixed(oxmTlvHeader));
           oxmTlvVlanVidRecord.put("tlv", getUint16Fixed(vid));
           
           Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
           GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
           oxmTlvRecord.put("match", oxmTlvVlanVidRecord);
           
           matches.add(oxmTlvRecord);
           
           if (this.delayedVlanPcp != null)
              matches.add(this.delayedVlanPcp);
           else 
              this.isVlanVid = true;
           
         /*
          * VLAN_PCP
          */
         } else if (key.equals("dl_vlan_pcp")) {
            
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_vlan_pcp");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
            
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                               (OXMField.OFPXMT_OFB_VLAN_PCP.getValue() << 9) | 
                               (0 << 8) |
                               1;
            byte tmp = U8.t(Short.valueOf((String) args.get(key)));
            
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint8Fixed(tmp));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            if (this.isVlanVid == true)
               matches.add(oxmTlvRecord);
            else
               this.delayedVlanPcp = oxmTlvRecord;

            /* 
             * MATCH - IPV4_SRC
             */
         } else if (key.equals("nw_src")) {
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                (OXMField.OFPXMT_OFB_IPV4_SRC.getValue() << 9) | 
                (0 << 8) |
                4;
             
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ipv4_src");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint32Fixed(get_ipv4((String) args.get(key))));

            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            if (this.isEtherType)
               matches.add(oxmTlvRecord);
            else 
               this.delayedIpv4Src = oxmTlvRecord;

            /* 
             * MATCH - IPV4_DST
             */
         } else if (key.equals("nw_dst")) {
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                (OXMField.OFPXMT_OFB_IPV4_DST.getValue() << 9) | 
                (0 << 8) |
                4;
             
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ipv4_dst");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint32Fixed(get_ipv4((String) args.get(key))));

            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);

            if (this.isEtherType)
               matches.add(oxmTlvRecord);
            else 
               this.delayedIpv4Dst = oxmTlvRecord;

            /* 
             * MATCH - IPV6_SRC
             */
         } else if (key.equals("ipv6_dst")) {
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                (OXMField.OFPXMT_OFB_IPV6_DST.getValue() << 9) | 
                (0 << 8) |
                16;
             
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ipv6_dst");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint128Fixed(get_ipv6((String) args.get(key))));
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
             
            matches.add(oxmTlvRecord);

            
            /* 
             * MATCH - IPV6_SRC
             */
         } else if (key.equals("ipv6_dst")) {
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                (OXMField.OFPXMT_OFB_IPV6_DST.getValue() << 9) | 
                (0 << 8) |
                16;
             
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ipv6_dst");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint128Fixed(get_ipv6((String) args.get(key))));
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
             
            matches.add(oxmTlvRecord);
            

         /*
          * OXM_OF_IP_DSCP 
          */
         } else if (key.equals("ip_dscp")) {
            
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ip_dscp");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
            
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                               (OXMField.OFPXMT_OFB_IP_DSCP.getValue() << 9) | 
                               (0 << 8) |
                               1;
            byte tmp = U8.t(Short.valueOf((String) args.get(key)));
            
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint8Fixed(tmp));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            matches.add(oxmTlvRecord);

         /*
          * OXM_OF_IP_ECN 
          */
         } else if (key.equals("ip_ecn")) {
            
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ip_ecn");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
            
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                               (OXMField.OFPXMT_OFB_IP_ECN.getValue() << 9) | 
                               (0 << 8) |
                               1;
            byte tmp = U8.t(Short.valueOf((String) args.get(key)));
            
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint8Fixed(tmp));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            matches.add(oxmTlvRecord);

         /*
          * OXM_OF_IP_PROTO 
          */
         } else if (key.equals("ip_proto")) {
            
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ip_proto");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
            
            int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                               (OXMField.OFPXMT_OFB_IP_PROTO.getValue() << 9) | 
                               (0 << 8) |
                               1;
            byte tmp = U8.t(Short.valueOf((String) args.get(key)));
            
            oxmTlvFieldRecord.put("header", getUint32Fixed(oxmTlvHeader));
            oxmTlvFieldRecord.put("tlv", getUint8Fixed(tmp));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            matches.add(oxmTlvRecord);
            
         }
      }

      Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
      GenericArray<GenericRecord> oxmTlvsArray = new GenericData.Array<>(Schema.createArray(oxmTlvSchema), matches);
      
      Schema oxmTlvFieldsSchema = protocol.getType("of.oxm_tlv_fields");
      GenericRecord oxmTlvFieldsRecord = new GenericData.Record(oxmTlvFieldsSchema);
      oxmTlvFieldsRecord.put("oxm_tlvs", oxmTlvsArray);

      /*
       * Build match header
       */
      Schema matchHeaderSchema = protocol.getType("of.match_header");
      GenericRecordBuilder matchHeaderBuilder = new GenericRecordBuilder(matchHeaderSchema);
      GenericRecord matchHeaderRecord = matchHeaderBuilder.build();
      
      /*
       * Calculating oxm_tlvs length
       */
      ByteArrayOutputStream oxmOut = new ByteArrayOutputStream();
      DatumWriter<GenericRecord> oxmWriter = new GenericDatumWriter<GenericRecord>(oxmTlvFieldsSchema);
      Encoder oxmEncoder = EncoderFactory.get().binaryNonEncoder(oxmOut, null);
      
      int closingPadLength = 4;
      
      try {
         oxmWriter.write(oxmTlvFieldsRecord, oxmEncoder);
         oxmEncoder.flush();
         
         int matchLength = oxmOut.size() + 4;
         closingPadLength = (int) ((matchLength + 7)/8*8 - matchLength);
         
         Schema uint16Schema = protocol.getType("of.uint_16");
         
         byte len[] = {(byte)(matchLength >> 8), (byte)(255 & matchLength)}; 
         GenericData.Fixed lenght = new GenericData.Fixed(uint16Schema, len);
         
         matchHeaderRecord.put("length", lenght);
      } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }      
      
      /*
       * Build closing pad
       */
      ByteBuffer clP = ByteBuffer.allocate(closingPadLength);
//      GenericData.Fixed closingPadRecord = new GenericData.Fixed(Schema.createFixed("", "", "of", closingPadLength), clP.toByteArray());
  
      /*
       * Assemble ofp_match structure
       */
      Schema ofpMatchSchema = protocol.getType("of.ofp_match");
      GenericRecord ofpMatchRecord = new GenericData.Record(ofpMatchSchema);
      ofpMatchRecord.put("header", matchHeaderRecord);
      ofpMatchRecord.put("fields", oxmTlvFieldsRecord);
      ofpMatchRecord.put("closing_pad", clP);

      
/* I N S T R U C T I O N S */      
      /*
       * Build Instruction records
       * Build GoTo Instruction for Test
       */
      Schema ofpInstrSchema = protocol.getType("of.ofp_instruction");
  /*    GenericRecord ofpInstrRecord = new GenericData.Record(ofpInstrSchema);
      
      Schema ofpInstrGoToSchema = protocol.getType("of.ofp_instruction_goto_table");
      GenericRecordBuilder instrBuilder = new GenericRecordBuilder(ofpInstrGoToSchema);
      GenericRecord ofpInstrGoToRecord = instrBuilder.build();
      ofpInstrRecord.put("instruction", ofpInstrGoToRecord);
      
      instructions.add(ofpInstrRecord);
*/
      GenericArray<GenericRecord> instrArray = new GenericData.Array<>(Schema.createArray(ofpInstrSchema), instructions);
      
      /* 
       * Create Instruction Set
       */
      Schema instrSetSchema = protocol.getType("of.instruction_set");
      GenericRecord instrSetRecord = new GenericData.Record(instrSetSchema);
      instrSetRecord.put("set", instrArray);

/* F L O W  M O D  M E S S A G E */  
      /*
       * Create FlowMod Header
       */
      Schema flowModHeaderSchema = protocol.getType("of.flow_mod_header");
      GenericRecordBuilder flowModHeaderBuilder = new GenericRecordBuilder(flowModHeaderSchema);
      GenericRecord flowModHeaderRecord = flowModHeaderBuilder.build();
      
      /*
       * Assemble Flow_mod message
       */
      ofpFlowModRecord.put("header", flowModHeaderRecord);      
      ofpFlowModRecord.put("base", flowModBodyRecord);
      ofpFlowModRecord.put("match", ofpMatchRecord);
      ofpFlowModRecord.put("instructions", instrSetRecord);
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpFlowModSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      
      try {
         writer.write(ofpFlowModRecord, encoder);
         encoder.flush();
         
         Schema uint16Schema = protocol.getType("of.uint_16");
               
         byte len[] = {(byte)(out.size() >> 8), (byte)(255 & out.size())}; 
         GenericData.Fixed lenght = new GenericData.Fixed(uint16Schema, len);
         
         flowModHeaderRecord.put("length", lenght);
         
         ofpFlowModRecord.put("header", flowModHeaderRecord); 
         
         out.reset();
         writer.write(ofpFlowModRecord, encoder);
         encoder.flush();
         
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return out;
   }
   
   public ByteArrayOutputStream getFlowModMessage (ByteArrayOutputStream out, List<Match> matches, List<Instruction> instructions) {
      
      return out;
   }
   
   /*
    * Utilities
    */
   
   public GenericRecord parseActionString(String actionstr) {
      
      Schema ofpActionSchema = protocol.getType("of.ofp_action");      
      List<GenericRecord> actions = new LinkedList<GenericRecord>();
      
      GenericRecord actionSetRecord = null;

      if (actionstr != null) {
          actionstr = actionstr.toLowerCase();
          for (String subaction : actionstr.split(",")) {
              String action = subaction.split("[=:]")[0];
              
              GenericRecord ofpActionRecord = null;
              
              if (action.equals("output")) {
                 ofpActionRecord = decode_output(subaction);
              } else if (action.equals("set_field_eth_dst")) {
                 ofpActionRecord = decode_set_field_eth_dst(subaction);
              }  else if (action.equals("set_field_eth_src")) {
                 ofpActionRecord = decode_set_field_eth_src(subaction);
              }  else if (action.equals("set_field_vlan_vid")) {
                 ofpActionRecord = decode_set_field_vlan_vid(subaction);
              }  else if (action.equals("set_field_mpls_label")) {
                 ofpActionRecord = decode_set_field_mpls_label(subaction);
              }  else if (action.equals("pop_vlan")) {
                 ofpActionRecord = decode_pop_vlan(subaction);
              }  else if (action.equals("push_mpls")) {
                 ofpActionRecord = decode_push_mpls(subaction);
              }
/*              else if (action.equals("enqueue")) {
                  subaction_struct = decode_enqueue(subaction);
              }
              else if (action.equals("strip-vlan")) {
                  subaction_struct = decode_strip_vlan(subaction);
              }
              else if (action.equals("set-vlan-id")) {
                  subaction_struct = decode_set_vlan_id(subaction);
              }
              else if (action.equals("set-vlan-priority")) {
                  subaction_struct = decode_set_vlan_priority(subaction);
              }
              else if (action.equals("set-src-mac")) {
                  subaction_struct = decode_set_src_mac(subaction);
              }
              else if (action.equals("set-dst-mac")) {
                  subaction_struct = decode_set_dst_mac(subaction);
              }
              else if (action.equals("set-tos-bits")) {
                  subaction_struct = decode_set_tos_bits(subaction);
              }
              else if (action.equals("set-src-ip")) {
                  subaction_struct = decode_set_src_ip(subaction);
              }
              else if (action.equals("set-dst-ip")) {
                  subaction_struct = decode_set_dst_ip(subaction);
              }
              else if (action.equals("set-src-port")) {
                  subaction_struct = decode_set_src_port(subaction);
              }
              else if (action.equals("set-dst-port")) {
                  subaction_struct = decode_set_dst_port(subaction);
              }
              else {
      //            log.error("  Unexpected action '{}', '{}'", action, subaction);
              }*/
              actions.add(ofpActionRecord);
          }
          

          GenericArray<GenericRecord> actionArray = new GenericData.Array<> (Schema.createArray(ofpActionSchema), actions);
          Schema actionSetSchema = protocol.getType("of.action_set");
          actionSetRecord = new GenericData.Record(actionSetSchema);
          actionSetRecord.put("set", actionArray);
      }
      
      return actionSetRecord;
  }
   
   private GenericRecord decode_output(String subaction) {

      Matcher n;      
      
      Schema ofpActionSchema = protocol.getType("of.ofp_action");
      GenericRecord ofpActionBaseRecord = new GenericData.Record(ofpActionSchema);
        
    
      Schema ofpActionOutSchema = protocol.getType("of.ofp_action_output");
      GenericRecordBuilder actionBuilder = new GenericRecordBuilder(ofpActionOutSchema);
      GenericRecord ofpActionOutRecord = actionBuilder.build();
      
      n = Pattern.compile("output=(?:((?:0x)?\\d+)|(all)|(controller)|(local)|(ingress-port)|(normal)|(flood))").matcher(subaction);
      if (n.matches()) {

          int port = OFPort.OFPP_NONE.getValue();
          if (n.group(1) != null) {
              try {
                  port = get_int(n.group(1));
              }
              catch (NumberFormatException e) {
                 // log.debug("  Invalid port in: '{}' (error ignored)", subaction);
                  return null;
              }
          }
          else if (n.group(2) != null)
              port = OFPort.OFPP_ALL.getValue();
          else if (n.group(3) != null)
              port = OFPort.OFPP_CONTROLLER.getValue();
          else if (n.group(4) != null)
              port = OFPort.OFPP_LOCAL.getValue();
          else if (n.group(5) != null)
              port = OFPort.OFPP_IN_PORT.getValue();
          else if (n.group(6) != null)
              port = OFPort.OFPP_NORMAL.getValue();
          else if (n.group(7) != null)
              port = OFPort.OFPP_FLOOD.getValue();
 
          ofpActionOutRecord.put("port", getUint32Fixed(port)); 
      }
      else {
      //    log.error("  Invalid action: '{}'", subaction);
          return null;
      }
      
      ofpActionBaseRecord.put("action", ofpActionOutRecord);
      
      return ofpActionBaseRecord;
  }
   
   private GenericRecord decode_set_field_eth_dst(String subaction) {

      Matcher n;      
      
      Schema ofpActionSchema = protocol.getType("of.ofp_action");
      GenericRecord ofpActionRecord = new GenericData.Record(ofpActionSchema);
      List <GenericRecord> matches = new ArrayList<>();
      
      Schema actionSetFieldHeaderSchema = protocol.getType("of.action_set_field_header");
      GenericRecordBuilder headerBuilder = new GenericRecordBuilder(actionSetFieldHeaderSchema);
      GenericRecord actionSetFieldHeaderRecord = headerBuilder.build();
    
      Schema ofpActionSetFieldSchema = protocol.getType("of.ofp_action_set_field");
      GenericRecord ofpActionSetFieldRecord = new GenericData.Record(ofpActionSetFieldSchema);

      n = Pattern.compile("set_field_eth_dst=(?:((?:0x)?\\d+))").matcher(subaction);
      if (n.matches()) {

          long eth = 0;
          if (n.group(1) != null) {
              try {
                  eth = get_long(n.group(1));
              }
              catch (NumberFormatException e) {
                 // log.debug("  Invalid port in: '{}' (error ignored)", subaction);
                  return null;
              }
          }
          
          Schema oxmTlvEthDstSchema = protocol.getType("of.oxm_tlv_eth_dst");
          GenericRecord oxmTlvEthDstRecord = new GenericData.Record(oxmTlvEthDstSchema);
          
          int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                             (OXMField.OFPXMT_OFB_ETH_DST.getValue() << 9) | 
                             (0 << 8) |
                             6;
          
             
          oxmTlvEthDstRecord.put("header", getUint32Fixed(oxmTlvHeader));
          oxmTlvEthDstRecord.put("tlv", getUint48Fixed(eth));
          
          Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
          GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
          oxmTlvRecord.put("match", oxmTlvEthDstRecord);
          
          matches.add(oxmTlvRecord);
          
          GenericArray<GenericRecord> oxmTlvsArray = new GenericData.Array<>(Schema.createArray(oxmTlvSchema), matches);
          
          Schema oxmTlvFieldsSchema = protocol.getType("of.oxm_tlv_fields");
          GenericRecord oxmTlvFieldsRecord = new GenericData.Record(oxmTlvFieldsSchema);
          oxmTlvFieldsRecord.put("oxm_tlvs", oxmTlvsArray);

          
          /*
           * Calculating oxm_tlvs length
           */
          ByteArrayOutputStream oxmOut = new ByteArrayOutputStream();
          DatumWriter<GenericRecord> oxmWriter = new GenericDatumWriter<GenericRecord>(oxmTlvFieldsSchema);
          Encoder oxmEncoder = EncoderFactory.get().binaryNonEncoder(oxmOut, null);
          
          int closingPadLength = 4;
          
          try {
             oxmWriter.write(oxmTlvFieldsRecord, oxmEncoder);
             oxmEncoder.flush();
             
             short matchLength = (short)oxmOut.size();
             closingPadLength = (int)(((matchLength + 4) + 7)/8*8 - (matchLength + 4));
             actionSetFieldHeaderRecord.put("length", getUint16Fixed(matchLength));

          } catch (IOException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
          }      
          
          /*
           * Build closing pad
           */
          ByteBuffer clP = ByteBuffer.allocate(closingPadLength);
     
          /*
           * Assemble action structure
           */
          ofpActionSetFieldRecord.put("header", actionSetFieldHeaderRecord);
          ofpActionSetFieldRecord.put("fields", oxmTlvFieldsRecord);
          ofpActionSetFieldRecord.put("closing_pad", clP);            /*
           * Calculate ofp_instruction_write_actions length 
           */
          
          ByteArrayOutputStream actOut = new ByteArrayOutputStream();
          DatumWriter<GenericRecord> actWriter = new GenericDatumWriter<GenericRecord>(ofpActionSetFieldSchema);
          Encoder instrEncoder = EncoderFactory.get().binaryNonEncoder(actOut, null);
         
          try {
             actWriter.write(ofpActionSetFieldRecord, instrEncoder);
             instrEncoder.flush();
             
             Schema uint16Schema = protocol.getType("of.uint_16");
             
             byte len[] = {(byte)(actOut.size() >> 8), (byte)(255 & actOut.size())}; 
             GenericData.Fixed lenght = new GenericData.Fixed(uint16Schema, len);
             
             actionSetFieldHeaderRecord.put("length", getUint16Fixed((short)actOut.size()));
             ofpActionSetFieldRecord.put("header", actionSetFieldHeaderRecord);
          } catch (IOException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
          }
      }
      else {
          return null;
      }
      
      ofpActionRecord.put("action", ofpActionSetFieldRecord);
      
      return ofpActionRecord;
  }
   
  private GenericRecord decode_set_field_eth_src(String subaction) {

      Matcher n;      
      
      Schema ofpActionSchema = protocol.getType("of.ofp_action");
      GenericRecord ofpActionRecord = new GenericData.Record(ofpActionSchema);
      List <GenericRecord> matches = new ArrayList<>();
      
      Schema actionSetFieldHeaderSchema = protocol.getType("of.action_set_field_header");
      GenericRecordBuilder headerBuilder = new GenericRecordBuilder(actionSetFieldHeaderSchema);
      GenericRecord actionSetFieldHeaderRecord = headerBuilder.build();
    
      Schema ofpActionSetFieldSchema = protocol.getType("of.ofp_action_set_field");
      GenericRecord ofpActionSetFieldRecord = new GenericData.Record(ofpActionSetFieldSchema);

      //TODO implement format XX:XX:XX:XX:XX:XX using the precious get_mac_addr()
      n = Pattern.compile("set_field_eth_src=(?:((?:0x)?\\d+))").matcher(subaction);
      if (n.matches()) {

          long eth = 0;
          if (n.group(1) != null) {
              try {
                  eth = get_long(n.group(1));
              }
              catch (NumberFormatException e) {
                 // log.debug("  Invalid port in: '{}' (error ignored)", subaction);
                  return null;
              }
          }
          
          Schema oxmTlvEthSrcSchema = protocol.getType("of.oxm_tlv_eth_src");
          GenericRecord oxmTlvEthSrcRecord = new GenericData.Record(oxmTlvEthSrcSchema);
          
          int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                             (OXMField.OFPXMT_OFB_ETH_SRC.getValue() << 9) | 
                             (0 << 8) |
                             6;
          
          oxmTlvEthSrcRecord.put("header", getUint32Fixed(oxmTlvHeader));
          oxmTlvEthSrcRecord.put("tlv", getUint48Fixed(eth));
          
          Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
          GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
          oxmTlvRecord.put("match", oxmTlvEthSrcRecord);
          
          matches.add(oxmTlvRecord);
          
          GenericArray<GenericRecord> oxmTlvsArray = new GenericData.Array<>(Schema.createArray(oxmTlvSchema), matches);
          
          Schema oxmTlvFieldsSchema = protocol.getType("of.oxm_tlv_fields");
          GenericRecord oxmTlvFieldsRecord = new GenericData.Record(oxmTlvFieldsSchema);
          oxmTlvFieldsRecord.put("oxm_tlvs", oxmTlvsArray);
          
          /*
           * Calculating oxm_tlvs length
           */
          ByteArrayOutputStream oxmOut = new ByteArrayOutputStream();
          DatumWriter<GenericRecord> oxmWriter = new GenericDatumWriter<GenericRecord>(oxmTlvFieldsSchema);
          Encoder oxmEncoder = EncoderFactory.get().binaryNonEncoder(oxmOut, null);
          
          int closingPadLength = 4;
          
          try {
             oxmWriter.write(oxmTlvFieldsRecord, oxmEncoder);
             oxmEncoder.flush();
             
             short matchLength = (short)oxmOut.size();
             closingPadLength = (int)(((matchLength + 4) + 7)/8*8 - (matchLength + 4));
             actionSetFieldHeaderRecord.put("length", getUint16Fixed(matchLength));

          } catch (IOException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
          }      
          
          /*
           * Build closing pad
           */
          ByteBuffer clP = ByteBuffer.allocate(closingPadLength);
     
          /*
           * Assemble action structure
           */
          ofpActionSetFieldRecord.put("header", actionSetFieldHeaderRecord);
          ofpActionSetFieldRecord.put("fields", oxmTlvFieldsRecord);
          ofpActionSetFieldRecord.put("closing_pad", clP);            /*
           * Calculate ofp_instruction_write_actions length 
           */
          
          ByteArrayOutputStream actOut = new ByteArrayOutputStream();
          DatumWriter<GenericRecord> actWriter = new GenericDatumWriter<GenericRecord>(ofpActionSetFieldSchema);
          Encoder instrEncoder = EncoderFactory.get().binaryNonEncoder(actOut, null);
         
          try {
             actWriter.write(ofpActionSetFieldRecord, instrEncoder);
             instrEncoder.flush();
             
             Schema uint16Schema = protocol.getType("of.uint_16");
             
             byte len[] = {(byte)(actOut.size() >> 8), (byte)(255 & actOut.size())}; 
             GenericData.Fixed lenght = new GenericData.Fixed(uint16Schema, len);
             
             actionSetFieldHeaderRecord.put("length", getUint16Fixed((short)actOut.size()));
             ofpActionSetFieldRecord.put("header", actionSetFieldHeaderRecord);
          } catch (IOException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
          }
      }
      else {
          return null;
      }
      
      ofpActionRecord.put("action", ofpActionSetFieldRecord);
      
      return ofpActionRecord;
  }
  
  private GenericRecord decode_push_mpls(String subaction) { 
     
     Matcher n;
     
     Schema ofpActionSchema = protocol.getType("of.ofp_action");
     GenericRecord ofpActionRecord = new GenericData.Record(ofpActionSchema);
     
     Schema actionPushMplsHeaderSchema = protocol.getType("of.action_push_mpls_header");
     GenericRecordBuilder headerBuilder = new GenericRecordBuilder(actionPushMplsHeaderSchema);
     GenericRecord actionPushMplsHeaderRecord = headerBuilder.build();
     
     Schema ofpActionPushMplsSchema = protocol.getType("of.ofp_action_push_mpls");
     GenericRecord ofpActionPushMplsRecord = new GenericData.Record(ofpActionPushMplsSchema);

     ofpActionPushMplsRecord.put("header", actionPushMplsHeaderRecord);
     ofpActionPushMplsRecord.put("pad", getPad2((short)0));

     n = Pattern.compile("push_mpls=(?:((?:0x)?\\d+))").matcher(subaction);
     if (n.matches()) {

         short ethertype = 0;
         if (n.group(1) != null) {
             try {
                ethertype = U16.t(Integer.valueOf(((String) n.group(1)).replaceFirst("0x", ""), 16));
                      //get_short(n.group(1));
             }
             catch (NumberFormatException e) {
                // log.debug("  Invalid port in: '{}' (error ignored)", subaction);
                 return null;
             }
         }
         ofpActionPushMplsRecord.put("ethertype", getUint16Fixed(ethertype));
     }

     ofpActionRecord.put("action", ofpActionPushMplsRecord);

     return ofpActionRecord;
  }
  
  private GenericRecord decode_pop_vlan(String subaction) { 
     
     Matcher n;
     
     Schema ofpActionSchema = protocol.getType("of.ofp_action");
     GenericRecord ofpActionRecord = new GenericData.Record(ofpActionSchema);
     
     Schema actionPopVlanHeaderSchema = protocol.getType("of.action_pop_vlan_header");
     GenericRecordBuilder headerBuilder = new GenericRecordBuilder(actionPopVlanHeaderSchema);
     GenericRecord actionPopVlanHeaderRecord = headerBuilder.build();
     
     Schema ofpActionPopVlanSchema = protocol.getType("of.ofp_action_pop_vlan");
     GenericRecord ofpActionPopVlanRecord = new GenericData.Record(ofpActionPopVlanSchema);
     
     ofpActionPopVlanRecord.put("header", actionPopVlanHeaderRecord);
     ofpActionPopVlanRecord.put("pad", getPad4(0));

     ofpActionRecord.put("action", ofpActionPopVlanRecord);

     return ofpActionRecord;
  }
  
  private GenericRecord decode_set_field_vlan_vid(String subaction) {

      Matcher n;      
      
      Schema ofpActionSchema = protocol.getType("of.ofp_action");
      GenericRecord ofpActionRecord = new GenericData.Record(ofpActionSchema);
      List <GenericRecord> matches = new ArrayList<>();
      
      Schema actionSetFieldHeaderSchema = protocol.getType("of.action_set_field_header");
      GenericRecordBuilder headerBuilder = new GenericRecordBuilder(actionSetFieldHeaderSchema);
      GenericRecord actionSetFieldHeaderRecord = headerBuilder.build();
    
      Schema ofpActionSetFieldSchema = protocol.getType("of.ofp_action_set_field");
      GenericRecord ofpActionSetFieldRecord = new GenericData.Record(ofpActionSetFieldSchema);

      n = Pattern.compile("set_field_vlan_vid=(?:((?:0x)?\\d+))").matcher(subaction);
      if (n.matches()) {

          short vid = 0;
          if (n.group(1) != null) {
              try {
                  vid = get_short(n.group(1));
              }
              catch (NumberFormatException e) {
                 // log.debug("  Invalid port in: '{}' (error ignored)", subaction);
                  return null;
              }
          }
          
          Schema oxmTlvVlanVidSchema = protocol.getType("of.oxm_tlv_vlan_vid");
          GenericRecord oxmTlvVlanVidRecord = new GenericData.Record(oxmTlvVlanVidSchema);
          
          int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                             (OXMField.OFPXMT_OFB_VLAN_VID.getValue() << 9) | 
                             (0 << 8) |
                             2;
          
          oxmTlvVlanVidRecord.put("header", getUint32Fixed(oxmTlvHeader));
          oxmTlvVlanVidRecord.put("tlv", getUint16Fixed(vid));
          
          Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
          GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
          oxmTlvRecord.put("match", oxmTlvVlanVidRecord);
          
          matches.add(oxmTlvRecord);
          
          GenericArray<GenericRecord> oxmTlvsArray = new GenericData.Array<>(Schema.createArray(oxmTlvSchema), matches);
          
          Schema oxmTlvFieldsSchema = protocol.getType("of.oxm_tlv_fields");
          GenericRecord oxmTlvFieldsRecord = new GenericData.Record(oxmTlvFieldsSchema);
          oxmTlvFieldsRecord.put("oxm_tlvs", oxmTlvsArray);
          
          /*
           * Calculating oxm_tlvs length
           */
          ByteArrayOutputStream oxmOut = new ByteArrayOutputStream();
          DatumWriter<GenericRecord> oxmWriter = new GenericDatumWriter<GenericRecord>(oxmTlvFieldsSchema);
          Encoder oxmEncoder = EncoderFactory.get().binaryNonEncoder(oxmOut, null);
          
          int closingPadLength = 4;
          
          try {
             oxmWriter.write(oxmTlvFieldsRecord, oxmEncoder);
             oxmEncoder.flush();
             
             short matchLength = (short)oxmOut.size();
             closingPadLength = (int)(((matchLength + 4) + 7)/8*8 - (matchLength + 4));
             actionSetFieldHeaderRecord.put("length", getUint16Fixed(matchLength));

          } catch (IOException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
          }      
          
          /*
           * Build closing pad
           */
          ByteBuffer clP = ByteBuffer.allocate(closingPadLength);
     
          /*
           * Assemble action structure
           */
          ofpActionSetFieldRecord.put("header", actionSetFieldHeaderRecord);
          ofpActionSetFieldRecord.put("fields", oxmTlvFieldsRecord);
          ofpActionSetFieldRecord.put("closing_pad", clP);            /*
           * Calculate ofp_instruction_write_actions length 
           */
          
          ByteArrayOutputStream actOut = new ByteArrayOutputStream();
          DatumWriter<GenericRecord> actWriter = new GenericDatumWriter<GenericRecord>(ofpActionSetFieldSchema);
          Encoder instrEncoder = EncoderFactory.get().binaryNonEncoder(actOut, null);
         
          try {
             actWriter.write(ofpActionSetFieldRecord, instrEncoder);
             instrEncoder.flush();
             
             Schema uint16Schema = protocol.getType("of.uint_16");
             
             byte len[] = {(byte)(actOut.size() >> 8), (byte)(255 & actOut.size())}; 
             GenericData.Fixed lenght = new GenericData.Fixed(uint16Schema, len);
             
             actionSetFieldHeaderRecord.put("length", getUint16Fixed((short)actOut.size()));
             ofpActionSetFieldRecord.put("header", actionSetFieldHeaderRecord);
          } catch (IOException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
          }
      }
      else {
          return null;
      }
      
      ofpActionRecord.put("action", ofpActionSetFieldRecord);
      
      return ofpActionRecord;
  }
  
  private GenericRecord decode_set_field_mpls_label(String subaction) {

     Matcher n;      
     
     Schema ofpActionSchema = protocol.getType("of.ofp_action");
     GenericRecord ofpActionRecord = new GenericData.Record(ofpActionSchema);
     List <GenericRecord> matches = new ArrayList<>();
     
     Schema actionSetFieldHeaderSchema = protocol.getType("of.action_set_field_header");
     GenericRecordBuilder headerBuilder = new GenericRecordBuilder(actionSetFieldHeaderSchema);
     GenericRecord actionSetFieldHeaderRecord = headerBuilder.build();
   
     Schema ofpActionSetFieldSchema = protocol.getType("of.ofp_action_set_field");
     GenericRecord ofpActionSetFieldRecord = new GenericData.Record(ofpActionSetFieldSchema);

     n = Pattern.compile("set_field_mpls_label=(?:((?:0x)?\\d+))").matcher(subaction);
     if (n.matches()) {

         int label = 0;
         if (n.group(1) != null) {
             try {
                 label = get_int(n.group(1));
             }
             catch (NumberFormatException e) {
                // log.debug("  Invalid port in: '{}' (error ignored)", subaction);
                 return null;
             }
         }
         
         Schema oxmTlvMplsLabelSchema = protocol.getType("of.oxm_tlv_mpls_label");
         GenericRecord oxmTlvMplsLabelRecord = new GenericData.Record(oxmTlvMplsLabelSchema);
         
         int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                            (OXMField.OFPXMT_OFB_MPLS_LABEL.getValue() << 9) | 
                            (0 << 8) |
                            3;
         
         oxmTlvMplsLabelRecord.put("header", getUint32Fixed(oxmTlvHeader));
         oxmTlvMplsLabelRecord.put("tlv", getUint24Fixed(label));
         
         Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
         GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
         oxmTlvRecord.put("match", oxmTlvMplsLabelRecord);
         
         matches.add(oxmTlvRecord);
         
         GenericArray<GenericRecord> oxmTlvsArray = new GenericData.Array<>(Schema.createArray(oxmTlvSchema), matches);
         
         Schema oxmTlvFieldsSchema = protocol.getType("of.oxm_tlv_fields");
         GenericRecord oxmTlvFieldsRecord = new GenericData.Record(oxmTlvFieldsSchema);
         oxmTlvFieldsRecord.put("oxm_tlvs", oxmTlvsArray);
         
         /*
          * Calculating oxm_tlvs length
          */
         ByteArrayOutputStream oxmOut = new ByteArrayOutputStream();
         DatumWriter<GenericRecord> oxmWriter = new GenericDatumWriter<GenericRecord>(oxmTlvFieldsSchema);
         Encoder oxmEncoder = EncoderFactory.get().binaryNonEncoder(oxmOut, null);
         
         int closingPadLength = 4;
         
         try {
            oxmWriter.write(oxmTlvFieldsRecord, oxmEncoder);
            oxmEncoder.flush();
            
            short matchLength = (short)oxmOut.size();
            closingPadLength = (int)(((matchLength + 4) + 7)/8*8 - (matchLength + 4));
            actionSetFieldHeaderRecord.put("length", getUint16Fixed(matchLength));

         } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }      
         
         /*
          * Build closing pad
          */
         ByteBuffer clP = ByteBuffer.allocate(closingPadLength);
    
         /*
          * Assemble action structure
          */
         ofpActionSetFieldRecord.put("header", actionSetFieldHeaderRecord);
         ofpActionSetFieldRecord.put("fields", oxmTlvFieldsRecord);
         ofpActionSetFieldRecord.put("closing_pad", clP);            /*
          * Calculate ofp_instruction_write_actions length 
          */
         
         ByteArrayOutputStream actOut = new ByteArrayOutputStream();
         DatumWriter<GenericRecord> actWriter = new GenericDatumWriter<GenericRecord>(ofpActionSetFieldSchema);
         Encoder instrEncoder = EncoderFactory.get().binaryNonEncoder(actOut, null);
        
         try {
            actWriter.write(ofpActionSetFieldRecord, instrEncoder);
            instrEncoder.flush();
            
            Schema uint16Schema = protocol.getType("of.uint_16");
            
            byte len[] = {(byte)(actOut.size() >> 8), (byte)(255 & actOut.size())}; 
            GenericData.Fixed lenght = new GenericData.Fixed(uint16Schema, len);
            
            actionSetFieldHeaderRecord.put("length", getUint16Fixed((short)actOut.size()));
            ofpActionSetFieldRecord.put("header", actionSetFieldHeaderRecord);
         } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
     }
     else {
         return null;
     }
     
     ofpActionRecord.put("action", ofpActionSetFieldRecord);
     
     return ofpActionRecord;
 }

   protected int getRecordLength (GenericRecord record, DatumWriter<GenericRecord> writer, Encoder encoder) {
      
      ByteArrayOutputStream temp = new ByteArrayOutputStream();
      
      try {
         writer.write(record, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return 0;
   }
   
   
   // Parse int as decimal, hex (start with 0x or #) or octal (starts with 0)
   private static long get_long(String str) {
       return (long)Long.decode(str);
   }
   
   // Parse int as decimal, hex (start with 0x or #) or octal (starts with 0)
   private static int get_int(String str) {
       return (int)Integer.decode(str);
   }
  
   // Parse short as decimal, hex (start with 0x or #) or octal (starts with 0)
   private static short get_short(String str) {
       return (short)(int)Integer.decode(str);
   }
  
   // Parse byte as decimal, hex (start with 0x or #) or octal (starts with 0)
   private static byte get_byte(String str) {
       return Integer.decode(str).byteValue();
   }

   private GenericData.Fixed getUint64Fixed (long in) {
      byte temp [] = {(byte)(in >> 56), (byte)(in >> 48), (byte)(in >> 40), (byte)(in >> 32), (byte)(in >> 24),(byte)(in >> 16), (byte)(in >> 8), (byte)(in) };
      GenericData.Fixed out = new GenericData.Fixed(uint_64Schema, temp);
      
      return out;
   }
   
   private GenericData.Fixed getUint48Fixed (long in) {
      byte temp [] = {(byte)(in >> 40), (byte)(in >> 32), (byte)(in >> 24),(byte)(in >> 16), (byte)(in >> 8), (byte)(in) };
      GenericData.Fixed out = new GenericData.Fixed(uint_48Schema, temp);
      
      return out;
   }
   
   private GenericData.Fixed getUint48Fixed (byte in []) {
      return new GenericData.Fixed(uint_48Schema, in);
   }
   
   private GenericData.Fixed getUint128Fixed (byte in []) {
      return new GenericData.Fixed(uint_128Schema, in);
   }
   
   private GenericData.Fixed getUint32Fixed (byte in []) {
      return new GenericData.Fixed(uint_32Schema, in);
   }
   
   private GenericData.Fixed getUint32Fixed (int in) {
      byte temp [] = {(byte)(in >> 24),(byte)(in >> 16), (byte)(in >> 8), (byte)(in) };
      GenericData.Fixed out = new GenericData.Fixed(uint_32Schema, temp);
      
      return out;
   }
   
   private GenericData.Fixed getUint8Fixed (byte in) {
      byte temp [] = {in};
      GenericData.Fixed out = new GenericData.Fixed(uint_8Schema, temp);
      
      return out;
   }
   
   private GenericData.Fixed getPad4 (int in) {
    // TODO Implement based on pad_4 schema  
      return getUint32Fixed(in);
   }
   
   private GenericData.Fixed getPad2 (short in) {
      // TODO Implement based on pad_2 schema    
      return getUint16Fixed(in);
   }
   
   
   private GenericData.Fixed getUint16Fixed (short in) {
      byte temp [] = {(byte)(in >> 8), (byte)(in) };
      GenericData.Fixed out = new GenericData.Fixed(uint_16Schema, temp);
      
      return out;
   }
   
   private GenericData.Fixed getUint24Fixed (int in) {
      byte temp [] = {(byte)(in >> 16), (byte)(in >> 8), (byte)(in) };
      GenericData.Fixed out = new GenericData.Fixed(uint_24Schema, temp);
      
      return out;
   }
   
   private int ipFromCIDR(String cidr, String which)
         throws IllegalArgumentException {
     String values[] = cidr.split("/");
     String[] ip_str = values[0].split("\\.");
     int ip = 0;
     ip += Integer.valueOf(ip_str[0]) << 24;
     ip += Integer.valueOf(ip_str[1]) << 16;
     ip += Integer.valueOf(ip_str[2]) << 8;
     ip += Integer.valueOf(ip_str[3]);
     int prefix = 32; // all bits are fixed, by default

     if (values.length >= 2)
         prefix = Integer.valueOf(values[1]);
     int mask = 32 - prefix;
     
     return ip;
   }
   
   private static byte[] get_mac_addr(String in) {
      byte[] macaddr = new byte[6];
      
      Matcher n = Pattern.compile("(?:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+))").matcher(in);
      
      if (n.matches()) {
         for (int i=0; i<6; i++) {
            if (n.group(i+1) != null) {
                try {
                    macaddr[i] = get_byte("0x" + n.group(i+1));
                }
                catch (NumberFormatException e) {
           //         log.debug("  Invalid src-mac in: '{}' (error ignored)", subaction);
                    return null;
                }
            }
            else { 
           //     log.debug("  Invalid src-mac in: '{}' (null, error ignored)", subaction);
                return null;
            }
        }
      } else
         return null;

      return macaddr;
  }
   
  private static byte[] get_ipv6(String in) {
      byte[] ipv6 = new byte[16];
      
      Matcher n = Pattern.compile("(?:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+))").matcher(in);
      
      if (n.matches()) {
         for (int i=0; i<8; i++) {
            if (n.group(i+1) != null) {
                try {
                   ipv6[2*i] = (byte) (get_short("0x" + n.group(i+1)) & 255);
                   ipv6[2*i+1] = (byte) (get_short("0x" + n.group(i+1)) >> 8);
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }
            else { 
                return null;
            }
        }
      } else
         return null;

      return ipv6;
  }
  
  private static byte[] get_ipv4(String in) {
     byte[] ipv4 = new byte[4];
     
     Matcher n = Pattern.compile("(?:(\\p{XDigit}+)\\.(\\p{XDigit}+)\\.(\\p{XDigit}+)\\.(\\p{XDigit}+))").matcher(in);
     
     if (n.matches()) {
        for (int i=0; i<4; i++) {
           if (n.group(i+1) != null) {
               try {
                  ipv4[i] = get_byte(n.group(i+1));
               }
               catch (NumberFormatException e) {
                   return null;
               }
           }
           else { 
               return null;
           }
       }
     } else
        return null;

     return ipv4;
 }
}
