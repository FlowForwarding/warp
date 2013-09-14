package org.flowforwarding.of.protocol.ofmessages;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.Protocol;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler;
import org.flowforwarding.of.protocol.ofstructures.IOFStructureBuilder;
import org.flowforwarding.of.protocol.ofstructures.OFStructureBuilder13;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionHandler;
import org.flowforwarding.of.protocol.ofstructures.Tuple;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificFixed;
import org.flowforwarding.of.util.U16;
import org.flowforwarding.of.util.U8;

public class OFMessageProvider13AvroProtocol implements IOFMessageProvider{
   
   private final String schemaSrc = "of_protocol_13.avpr";
   
   private Schema ofpHeaderSchema = null;
   
   private Schema helloHeaderSchema = null;
   private Schema ofpHelloSchema = null;
   
   private Schema echoRequestHeaderSchema = null;
   private Schema ofpEchoRequestSchema = null;
   
   private Schema echoReplyHeaderSchema = null;
   private Schema ofpEchoReplySchema = null;
   
   private Schema ofpSwitchFeaturesRequestSchema = null;
   private Schema switchFeaturesRequestHeaderSchema = null;
   
   private Schema ofpSwitchFeaturesReplySchema = null;
   private Schema switchFeaturesReplyHeaderSchema = null;
   
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
   private GenericRecord delayedIpv6Src = null;
   private GenericRecord delayedIpv6Dst = null;
   private GenericRecord delayedIpProto = null;
   private GenericRecord delayedTcpSrc = null;
   private GenericRecord delayedTcpDst = null;
   private GenericRecord delayedUdpSrc = null;
   private GenericRecord delayedUdpDst = null;

   

   private boolean isEtherType = false;
   private boolean isIpProto = false;
   
   
   private Protocol protocol = null;
   protected IOFMessageBuilder builder = null;
   protected IOFStructureBuilder structureBuilder = null;
   
   
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
         builder = new OFMessageBuilder13();
         structureBuilder = new OFStructureBuilder13();
         
         
         } catch (IOException e) {
         // TODO Auto-generated catch block
           e.printStackTrace();
         }

      ofpHeaderSchema =  protocol.getType("of.ofp_header");
      
      helloHeaderSchema = protocol.getType("of.ofp_hello_header");
      ofpHelloSchema = protocol.getType("of.ofp_hello");
      
      echoRequestHeaderSchema= protocol.getType("of.echo_request_header");
      ofpEchoRequestSchema = protocol.getType("of.ofp_echo_request");

      echoReplyHeaderSchema = protocol.getType("of.echo_reply_header");
      ofpEchoReplySchema = protocol.getType("of.ofp_echo_reply");

      
      ofpSwitchFeaturesRequestSchema =  protocol.getType("of.ofp_switch_features_request");
      switchFeaturesRequestHeaderSchema =  protocol.getType("of.ofp_switch_features_request_header");
      
      ofpSwitchFeaturesReplySchema =  protocol.getType("of.ofp_switch_features_reply");
      switchFeaturesReplyHeaderSchema =  protocol.getType("of.ofp_switch_features_reply_header");
      
      ofpSwitchConfigSchema = protocol.getType("of.ofp_switch_config");
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
   
   public OFMessageFlowModHandler buildFlowModMsg () {
      return builder.buildFlowMod();
   }
   
   public OFStructureInstructionHandler buildInstructionApplyActions () {
      return structureBuilder.buildInstructionApplyActions();
   }
   
   public OFStructureInstructionHandler buildInstructionWriteActions () {
      return structureBuilder.buildInstructionWriteActions();
   }
   
   public OFStructureInstructionHandler buildInstructionGotoTable () {
      return structureBuilder.buildInstructionGotoTable();
   }
   
   public OFStructureInstructionHandler buildInstructionClearActions () {
      return structureBuilder.buildInstructionClearActions();
   }
   
   public OFStructureInstructionHandler buildInstructionMeter () {
      return structureBuilder.buildInstructionMeter();
   }
   
   public OFStructureInstructionHandler buildInstructionWriteMetadata () {
      return structureBuilder.buildInstructionWriteMetadata();
   }
   
   private byte[] encodeMessage (Schema headerSchema, Schema bodySchema) {
      
      GenericRecord bodyRecord = new GenericData.Record(bodySchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(headerSchema);

      GenericRecord headerRecord = builder.build();
      bodyRecord.put("header", headerRecord);  
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpHelloSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(bodyRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return out.toByteArray();
   }
   
   public ByteArrayOutputStream getHello(ByteArrayOutputStream out) {
      
      GenericRecord ofpHelloRecord = new GenericData.Record(ofpHelloSchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(helloHeaderSchema);

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
   
   public byte[] encodeHelloMessage() {
      return encodeMessage(helloHeaderSchema, ofpHelloSchema);
   }
   
   public byte[] encodeEchoRequest() {
      return encodeMessage(echoRequestHeaderSchema, ofpEchoRequestSchema);
   }
   
   public byte[] encodeEchoReply() {
      return encodeMessage(echoReplyHeaderSchema, ofpEchoReplySchema);
   }
   
   public byte[] encodeSwitchFeaturesRequest () {
      return encodeMessage(switchFeaturesRequestHeaderSchema, ofpSwitchFeaturesRequestSchema);
   }
   
   public byte[] encodeSwitchFeaturesReply () {
      return encodeMessage(switchFeaturesReplyHeaderSchema, ofpSwitchFeaturesReplySchema);
   }

   public ByteArrayOutputStream getSwitchFeaturesRequest(ByteArrayOutputStream out) {
      
      GenericRecord ofpSwitchFeaturesRequestRecord = new GenericData.Record(ofpSwitchFeaturesRequestSchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(switchFeaturesRequestHeaderSchema);

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
      GenericRecordBuilder builder = new GenericRecordBuilder(switchFeaturesReplyHeaderSchema);
      
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
   
   protected GenericRecord getSwitchFeaturesReplyRecord (byte[] buffer) {
      
      try {
         GenericRecord featureReplyRecord = new GenericData.Record(ofpSwitchFeaturesSchema);
         GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(ofpSwitchFeaturesSchema);
         Decoder decoder = DecoderFactory.get().binaryDecoder(buffer, null);
         
         reader.read(featureReplyRecord, decoder);
         
         return featureReplyRecord;
    } catch (IOException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
       
       return null;
    }
   }
   
   protected GenericRecord getSwitchCofigRecord (byte[] buffer) {
      // TODO Improvs: make a protected getter to get general records.
      try {
         GenericRecord record = new GenericData.Record(ofpSwitchConfigSchema);
         GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(ofpSwitchConfigSchema);
         Decoder decoder = DecoderFactory.get().binaryDecoder(buffer, null);
         
         reader.read(record, decoder);
         
         return record;
    } catch (IOException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
       
       return null;
    }
   }
   
   public Long getDPID(byte[] buffer) {
      
      GenericRecord featureReplyRecord = getSwitchFeaturesReplyRecord (buffer);
      
      GenericData.Fixed dpid = (GenericData.Fixed) featureReplyRecord.get("datapath_id");
      return get_long(dpid);
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
   
   public byte[] encodeSwitchConfigRequest () {
      
      GenericRecord ofpGetConfigRequestRecord = new GenericData.Record(ofpGetConfigRequestSchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(ofpGetConfigRequestHeaderSchema);
      
      GenericRecord ofpGetConfigRequestHeaderRecord = builder.build();
      ofpGetConfigRequestRecord.put("header", ofpGetConfigRequestHeaderRecord);  
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpGetConfigRequestSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(ofpGetConfigRequestRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
     
      return out.toByteArray();
   }
   
   public byte[] encodeFlowMod (OFMessageFlowModHandler fmHandler) {
      
      Schema ofpFlowModSchema = protocol.getType("of.ofp_flow_mod");
      GenericRecord ofpFlowModRecord = new GenericData.Record(ofpFlowModSchema);
      
      GenericRecordBuilder builder = null;
      
      Schema flowModHeaderSchema = protocol.getType("of.flow_mod_header");
      builder = new GenericRecordBuilder (flowModHeaderSchema);
      GenericRecord flowModHeaderRecord = builder.build();
      
      Schema flowModBodySchema = protocol.getType("of.flow_mod_body_add");
      builder = new GenericRecordBuilder(flowModBodySchema);
      GenericRecord flowModBodyRecord = builder.build();
      
      GenericRecord ofpMatchRecord = null;
      Schema ofpMatchSchema = null;
      
      List <GenericRecord> instructions = new ArrayList<>();
      List <GenericRecord> matches = new ArrayList<>();
      
   // TODO Improvs: I dislike this *.getMatches().getIterator();
//      Iterator<Tuple<String, String>> matchIter = fmRef.getMatches().getIterator();
      List<Tuple<String, String>> matchList = fmHandler.getMatches().getMatches();
      for (Tuple<String, String> match : matchList) {
         //Tuple<String, String> match = matchIter.next();
         String name = match.getName();
         
         // TODO Improvs: Replace Switch with a structure... say, HashMap
         // TODO Improvs: How to control type compatibility between Shema types and incoming tlvs?
         switch (name) {
         case "ingress_port":
            matches.add(getMatch("of.oxm_tlv_ingress_port", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;
         
         case "in_phy_port":
            matches.add(getMatch("of.oxm_tlv_in_phy_port", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;

         case "metadata":
            matches.add(getMatch("of.oxm_tlv_metadata", getUint64Fixed(Long.parseLong(match.getValue()))));
            break;

         case "eth_dst":
            matches.add(getMatch("of.oxm_tlv_eth_dst", getUint48Fixed(get_mac_addr(match.getValue()))));
            break;
            
         case "eth_src":
            matches.add(getMatch("of.oxm_tlv_eth_src", getUint48Fixed(get_mac_addr(match.getValue()))));
            break;

         case "eth_type":
            short ethType = U16.t(Integer.valueOf(match.getValue().replaceFirst("0x", ""), 16));
            matches.add(getMatch("of.oxm_tlv_eth_type", getUint32Fixed(ethType)));
            break;            

         case "vlan_vid":
            short vid = U16.t(Integer.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_vlan_vid", getUint32Fixed(vid)));
            break;            

         case "ip_dscp":
            byte tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_ip_dscp", getUint8Fixed(tmp)));
            break;

         case "ip_ecn":
            tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_ip_ecn", getUint8Fixed(tmp)));
            break;

         case "ip_proto":
            tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_ip_proto", getUint8Fixed(tmp)));
            break;

         case "ipv4_src":
            matches.add(getMatch("of.oxm_tlv_ipv4_src", getUint32Fixed(get_ipv4(match.getValue()))));
            break;

         case "ipv4_dst":
            matches.add(getMatch("of.oxm_tlv_ipv4_dst", getUint32Fixed(get_ipv4(match.getValue()))));
            break;

         case "tcp_src":
            matches.add(getMatch("of.oxm_tlv_tcp_src", getUint16Fixed(U16.t(Integer.valueOf(match.getValue())))));
            break;
            
         case "tcp_dst":
            matches.add(getMatch("of.oxm_tlv_tcp_dst", getUint16Fixed(U16.t(Integer.valueOf(match.getValue())))));
            break;

         case "udp_src":
            matches.add(getMatch("of.oxm_tlv_udp_src", getUint16Fixed(U16.t(Integer.valueOf(match.getValue())))));
            break;

         case "udp_dst":
            matches.add(getMatch("of.oxm_tlv_udp_dst", getUint16Fixed(U16.t(Integer.valueOf(match.getValue())))));
            break;

         case "sctp_src":
            matches.add(getMatch("of.oxm_tlv_sctp_src", getUint16Fixed(U16.t(Integer.valueOf(match.getValue())))));
            break;

         case "sctp_dst":
            matches.add(getMatch("of.oxm_tlv_sctp_dst", getUint16Fixed(U16.t(Integer.valueOf(match.getValue())))));
            break;
            
         case "icmpv4_type":
            tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_icmpv4_type", getUint8Fixed(tmp)));
            break;

         case "icmpv4_code":
            tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_icmpv4_code", getUint8Fixed(tmp)));
            break;

         case "arp_op":
            matches.add(getMatch("of.oxm_tlv_arp_op", getUint16Fixed(U16.t(Integer.parseInt(match.getValue())))));
            break;

         case "arp_spa":
            matches.add(getMatch("of.oxm_tlv_arp_spa", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;

         case "arp_tpa":
            matches.add(getMatch("of.oxm_tlv_arp_tpa", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;
            
         case "arp_sha":
            matches.add(getMatch("of.oxm_tlv_arp_sha", getUint48Fixed(Long.parseLong(match.getValue()))));
            break;
            
         case "arp_tha":
            matches.add(getMatch("of.oxm_tlv_arp_tha", getUint48Fixed(Long.parseLong(match.getValue()))));
            break;

         case "ipv6_src":
            matches.add(getMatch("of.oxm_tlv_ipv6_src", getUint128Fixed(get_ipv6(match.getValue()))));
            break;

         case "ipv6_dst":
            matches.add(getMatch("of.oxm_tlv_ipv6_dst", getUint128Fixed(get_ipv6(match.getValue()))));
            break;

         case "ipv6_flabel":
            matches.add(getMatch("of.oxm_tlv_ipv6_flabel", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;

         case "icmpv6_type":
            tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_icmpv6_type", getUint8Fixed(tmp)));
            break;
         
         case "icmpv6_code":
            tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_icmpv6_code", getUint8Fixed(tmp)));
            break;

         case "ipv6_nd_target":
            matches.add(getMatch("of.oxm_tlv_ipv6_nd_target", getUint128Fixed(get_ipv6(match.getValue()))));
            break;

         case "ipv6_nd_sll":
            matches.add(getMatch("of.oxm_tlv_ipv6_nd_sll", getUint48Fixed(Long.parseLong(match.getValue()))));
            break;

         case "ipv6_nd_tll":
            matches.add(getMatch("of.oxm_tlv_ipv6_nd_tll", getUint48Fixed(Long.parseLong(match.getValue()))));
            break;

         case "mpls_label":
            matches.add(getMatch("of.oxm_tlv_mpls_label", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;

         case "mpls_tc":
            tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_mpls_tc", getUint8Fixed(tmp)));
            break;

         case "mpls_bos":
            tmp = U8.t(Short.valueOf(match.getValue()));
            matches.add(getMatch("of.oxm_tlv_mpls_bos", getUint8Fixed(tmp)));
            break;

         case "pbb_isid":
            matches.add(getMatch("of.oxm_tlv_pbb_isid", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;

         case "tunnel_id":
            matches.add(getMatch("of.oxm_tlv_tunnel_id", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;

         case "ipv6_exthdr":
            matches.add(getMatch("of.oxm_tlv_ipv6_exthdr", getUint32Fixed(Integer.parseInt(match.getValue()))));
            break;
            
         default:
            break;

         }
      }
      
      Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
      
      Schema oxmTlvFieldsSchema = protocol.getType("of.oxm_tlv_fields");
      GenericRecord oxmTlvFieldsRecord = new GenericData.Record(oxmTlvFieldsSchema);
      oxmTlvFieldsRecord.put("oxm_tlvs", new GenericData.Array<>(Schema.createArray(oxmTlvSchema), matches));

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
      ofpMatchSchema = protocol.getType("of.ofp_match");
      ofpMatchRecord = new GenericData.Record(ofpMatchSchema);
      ofpMatchRecord.put("header", matchHeaderRecord);
      ofpMatchRecord.put("fields", oxmTlvFieldsRecord);
      ofpMatchRecord.put("closing_pad", clP);
      
      
      // TODO Improvs: I dislike this *.getInstructions().getIterator();
      //Iterator<Tuple<String, OFStructureInstruction>> instrIter = fmRef.getInstructions().getIterator();
      List<Tuple<String, OFStructureInstructionHandler>> instrList = fmHandler.getInstructions().getInstructions();
      Schema instrHeaderSchema = null;
      Schema instrSchema = null;
      GenericRecord instrHeaderRecord = null;
      GenericRecord instrRecord = null;
      for (Tuple<String, OFStructureInstructionHandler> tuple : instrList) {
         boolean isActions = false;
         //Tuple<String, OFStructureInstruction> tuple = instrIter.next();
         String name = tuple.getName();
         OFStructureInstructionHandler instruction = tuple.getValue();
         
         // TODO Improvs: Replace Switch with a structure... say, HashMap
         // TODO Improvs: How to control type compatibility between Schema types and incoming tlvs?
         switch (name) {
         case "apply_actions":
            instrHeaderSchema = protocol.getType("of.instruction_apply_actions_header");
            instrSchema = protocol.getType("of.ofp_instruction_apply_actions");
            instrRecord = new GenericData.Record(instrSchema);
            isActions = true;
            break;
         case "write_actions":
            instrHeaderSchema = protocol.getType("of.instruction_write_actions_header");
            instrSchema = protocol.getType("of.ofp_instruction_write_actions");
            instrRecord = new GenericData.Record(instrSchema);
            isActions = true;            
            break;
         case "clear_actions":
            instrHeaderSchema = protocol.getType("of.instruction_clear_actions_header");
            instrSchema = protocol.getType("of.ofp_instruction_clear_actions");
            instrRecord = new GenericData.Record(instrSchema);
            isActions = true;            
            break;
         case "goto_table":
            instrHeaderSchema = protocol.getType("of.instruction_goto_table_header"); 
            instrSchema = protocol.getType("of.ofp_instruction_goto_table");
            instrRecord = new GenericData.Record(instrSchema);
            instrRecord.put("table_id", getUint8Fixed((byte)15));
            instrRecord.put("pad", getPad3(0));
            isActions = false;            
            break;
         case "write_metadata":
            instrHeaderSchema = protocol.getType("of.instruction_write_metadata_header");  
            instrSchema = protocol.getType("of.ofp_instruction_write_metadata");
            instrRecord = new GenericData.Record(instrSchema);
            isActions = false;            
            break;
         case "meter":
            instrHeaderSchema = protocol.getType("of.instruction_meter_header"); 
            instrSchema = protocol.getType("of.ofp_instruction_meter");
            instrRecord = new GenericData.Record(instrSchema);
            isActions = false;            
            break;
         }
         
         GenericRecordBuilder instrHeaderBuilder = new GenericRecordBuilder(instrHeaderSchema);
         instrHeaderRecord = instrHeaderBuilder.build();
         instrRecord.put("header", instrHeaderRecord);
         

         if (isActions) {
            List<Tuple<String, String>> actionList = instruction.getActions().getActions();
//            Iterator<Tuple<String, String>> actionIter = instruction.getActions().getIterator();
            Schema ofpActionSchema = protocol.getType("of.ofp_action");
            List<GenericRecord> actions = new LinkedList<GenericRecord>();
            GenericRecord actionSetRecord = null;

            for (Tuple<String, String> action : actionList) {
//               Tuple<String, String> action = actionIter.next();
               String actName = action.getName();

               GenericRecord ofpActionRecord = new GenericData.Record(ofpActionSchema);
               
               switch (actName) {
               case "output":
                  Schema ofpActionOutSchema = protocol.getType("of.ofp_action_output");
                  GenericRecordBuilder actionBuilder = new GenericRecordBuilder(ofpActionOutSchema);
                  GenericRecord ofpActionOutRecord = actionBuilder.build();
                  
                  ofpActionOutRecord.put("port", getUint32Fixed(Integer.decode(action.getValue())));
                  ofpActionRecord.put("action", ofpActionOutRecord);

                  break;
               
               default:
                  break;

               }
               actions.add(ofpActionRecord);
            }
                        
            Schema actionSetSchema = protocol.getType("of.action_set");
            actionSetRecord = new GenericData.Record(actionSetSchema);
            actionSetRecord.put("set", new GenericData.Array<> (Schema.createArray(ofpActionSchema), actions));
            
            instrRecord.put("header", instrHeaderRecord);
            instrRecord.put("actions", actionSetRecord);
            
            instrHeaderRecord.put("length", getUint16Fixed(calculateLength(instrSchema, instrRecord)));
            instrRecord.put("header", instrHeaderRecord);
         }

         Schema ofpInstructionSchema = protocol.getType("of.ofp_instruction");
         GenericRecord ofpInstructionRecord = new GenericData.Record(ofpInstructionSchema);
         ofpInstructionRecord.put("instruction", instrRecord);
         instructions.add(ofpInstructionRecord);
      }
      
      Schema ofpInstructionSchema = protocol.getType("of.ofp_instruction");
      Schema instructionSetSchema = protocol.getType("of.instruction_set");
      GenericRecord instructionSetRecord = new GenericData.Record(instructionSetSchema);
      instructionSetRecord.put("set", new GenericData.Array<> (Schema.createArray(ofpInstructionSchema), instructions));
      
      
      ofpFlowModRecord.put("header", flowModHeaderRecord);
      ofpFlowModRecord.put("base", flowModBodyRecord);
      ofpFlowModRecord.put("match", ofpMatchRecord);
      ofpFlowModRecord.put("instructions", instructionSetRecord);
      
      flowModHeaderRecord.put("length", getUint16Fixed(calculateLength(ofpFlowModSchema, ofpFlowModRecord)));
      ofpFlowModRecord.put("header", flowModHeaderRecord);
      
      ByteArrayOutputStream fmOut = new ByteArrayOutputStream();
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpFlowModSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(fmOut, null);
      
      try {
         writer.write(ofpFlowModRecord, encoder);
         encoder.flush();
      } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }       
      
      return fmOut.toByteArray();
   }
   
   public ByteArrayOutputStream getFlowMod (Map<String, Object> args, ByteArrayOutputStream out) {
      
      /*
       * Build FlowMod message
       */
      Schema ofpFlowModSchema = protocol.getType("of.ofp_flow_mod");
      GenericRecord ofpFlowModRecord = new GenericData.Record(ofpFlowModSchema);
      boolean isDelete = false;
      
      /*
       * Create FlowMod Body
       */
      Schema flowModBodySchema = null;
      if (args.containsKey("delete")) {
         flowModBodySchema = protocol.getType("of.flow_mod_body_delete");
         isDelete = true;
      }
      else {
         flowModBodySchema = protocol.getType("of.flow_mod_body_add");
         isDelete = false;
      }
      
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
         } else if ((key.equals("goto_table")) || (key.equals("write_metadata")) || (key.equals("meter"))) {
            continue;
            
         } else if ( (key.equals("apply_actions")) || (key.equals("write_actions")) || (key.equals("clear_actions"))) {
            actionSetRecord = parseActionString((String) args.get(key));
            
            /*
             * Build Write Actions Instruction for Test
             */
            Schema instrActionsHeaderSchema = null;
            if (key.equals("apply_actions")) 
               instrActionsHeaderSchema = protocol.getType("of.instruction_apply_actions_header");
            else if (key.equals("write_actions"))
               instrActionsHeaderSchema = protocol.getType("of.instruction_write_actions_header");
            else if (key.equals("clear_actions"))
               instrActionsHeaderSchema = protocol.getType("of.instruction_clear_actions_header");

            
            GenericRecordBuilder instrHeaderBuilder = new GenericRecordBuilder(instrActionsHeaderSchema);
            GenericRecord instrActionsHeaderRecord = instrHeaderBuilder.build();
            
            /*
             * Create ofp_instruction_write_actions
             */
            Schema ofpInstrWriteActionsSchema = protocol.getType("ofp_instruction_actions");
            GenericRecord ofpInstrWriteActionsRecord = new GenericData.Record(ofpInstrWriteActionsSchema);
            ofpInstrWriteActionsRecord.put("header", instrActionsHeaderRecord);
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
               
               instrActionsHeaderRecord.put("length", lenght);
               ofpInstrWriteActionsRecord.put("header", instrActionsHeaderRecord);
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
            
            
            /*Schema oxmTlvIngressPortSchema = protocol.getType("of.oxm_tlv_ingress_port");
            GenericRecord oxmTlvIngressPortRecord = new GenericData.Record(oxmTlvIngressPortSchema);
         
            int inPort = Integer.valueOf((String) args.get(key));
            oxmTlvIngressPortRecord.put("tlv", getUint32Fixed(inPort));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvIngressPortRecord);*/
            
            matches.add(getMatch("of.oxm_tlv_ingress_port", getUint32Fixed(Integer.valueOf((String) args.get(key)))));
            
            /*
             * MATCH - IN_PHY PORT
             */
         } else if (key.equals("in_phy_port")) {
            Schema oxmTlvInPhyPortSchema = protocol.getType("of.oxm_tlv_in_phy_port");
            GenericRecord oxmTlvInPhyPortRecord = new GenericData.Record(oxmTlvInPhyPortSchema);
               
            int inPort = Integer.valueOf((String) args.get(key));

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
               
            long mdata = Integer.valueOf((String) args.get(key));
            oxmTlvMetadataRecord.put("tlv", getUint64Fixed(mdata));
               
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvMetadataRecord);
               
            matches.add(oxmTlvRecord);
            
           /* 
            * MATCH - ETH_SRC
            */
         } else if (key.equals("dl_src")) {

            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_eth_src");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
            
            oxmTlvFieldRecord.put("tlv", getUint48Fixed(get_mac_addr((String) args.get(key))));
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            matches.add(oxmTlvRecord);

          /* 
           * MATCH - ETH_DST
           */
      } else if (key.equals("dl_dst")) {
      
         Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_eth_dst");
         GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);

         oxmTlvFieldRecord.put("tlv", getUint48Fixed(get_mac_addr((String) args.get(key))));
         Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
         GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
         oxmTlvRecord.put("match", oxmTlvFieldRecord);
         
         matches.add(oxmTlvRecord);
          
         /* 
          * MATCH - ETH_TYPE
          */
      } else if (key.equals("dl_type")) {
         Schema oxmTlvEthTypeSchema = protocol.getType("of.oxm_tlv_eth_type");
         GenericRecord oxmTlvEthTypeRecord = new GenericData.Record(oxmTlvEthTypeSchema);

         short ethType = U16.t(Integer.valueOf(((String) args.get(key)).replaceFirst("0x", ""), 16));
         
         oxmTlvEthTypeRecord.put("tlv", getUint16Fixed(ethType));
            
         Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
         GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
         oxmTlvRecord.put("match", oxmTlvEthTypeRecord);
   
         matches.add(oxmTlvRecord);
            
         if (this.delayedIpv4Src != null)
            matches.add(this.delayedIpv4Src);
         if (this.delayedIpv4Dst != null)
               matches.add(this.delayedIpv4Dst);
         if (this.delayedIpv6Src != null)
               matches.add(this.delayedIpv6Src);
         if (this.delayedIpv6Dst != null)
               matches.add(this.delayedIpv6Dst);
         if (this.delayedIpProto != null)
               matches.add(this.delayedIpProto);
            
         this.isEtherType = true;

        /*
         * VLAN_VID
         */
        } else if (key.equals("vlan_vid")) {
           
           Schema oxmTlvVlanVidSchema = protocol.getType("of.oxm_tlv_vlan_vid");
           GenericRecord oxmTlvVlanVidRecord = new GenericData.Record(oxmTlvVlanVidSchema);
           
           short vid = U16.t(Integer.valueOf((String) args.get(key)));
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
            
            byte tmp = U8.t(Short.valueOf((String) args.get(key)));
            oxmTlvFieldRecord.put("tlv", getUint8Fixed(tmp));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            if (this.isVlanVid == true)
               matches.add(oxmTlvRecord);
            else
               this.delayedVlanPcp = oxmTlvRecord;


            /* 
             * MATCH - IPV6_SRC
             */
         } else if (key.equals("ipv6_src")) {

            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ipv6_src");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("tlv", getUint128Fixed(get_ipv6((String) args.get(key))));
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
             
            if (this.isEtherType)
               matches.add(oxmTlvRecord);
            else 
               this.delayedIpv6Src = oxmTlvRecord;
            
            /* 
             * MATCH - IPV6_SRC
             */
         } else if (key.equals("ipv6_dst")) {

            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ipv6_dst");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("tlv", getUint128Fixed(get_ipv6((String) args.get(key))));
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
             
            if (this.isEtherType)
               matches.add(oxmTlvRecord);
            else 
               this.delayedIpv6Dst = oxmTlvRecord;
            

         /*
          * OXM_OF_IP_DSCP 
          */
         } else if (key.equals("ip_dscp")) {
            
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ip_dscp");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
            
            byte tmp = U8.t(Short.valueOf((String) args.get(key)));
            
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
            
            byte tmp = U8.t(Short.valueOf((String) args.get(key)));
            oxmTlvFieldRecord.put("tlv", getUint8Fixed(tmp));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            matches.add(oxmTlvRecord);

         /*
          * OXM_OF_IP_PROTO 
          */
         } else if (key.equals("nw_proto")) {
            
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ip_proto");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
            
            byte tmp = U8.t(Short.valueOf((String) args.get(key)));
            oxmTlvFieldRecord.put("tlv", getUint8Fixed(tmp));
            
            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);
            
            if (this.isEtherType) {
               matches.add(oxmTlvRecord);
               this.isIpProto = true;
            }
            else {
               this.delayedIpProto = oxmTlvRecord;
            }
            
            if (this.delayedTcpSrc != null)
               matches.add(this.delayedTcpSrc);
            if (this.delayedTcpDst != null)
               matches.add(this.delayedTcpDst);
            if (this.delayedUdpSrc != null)
               matches.add(this.delayedUdpSrc);
            if (this.delayedUdpDst != null)
               matches.add(this.delayedUdpDst);
/*            if (this.delayedIpv6Src != null)
               matches.add(this.delayedIpv6Src);
            if (this.delayedIpv6Dst != null)
               matches.add(this.delayedIpv6Dst);
            if (this.delayedIpProto != null)
               matches.add(this.delayedIpProto);*/

            /* 
             * MATCH - IPV4_SRC
             */
         } else if (key.equals("nw_src")) {
            
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ipv4_src");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
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
             
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_ipv4_dst");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("tlv", getUint32Fixed(get_ipv4((String) args.get(key))));

            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);

            if (this.isEtherType)
               matches.add(oxmTlvRecord);
            else 
               this.delayedIpv4Dst = oxmTlvRecord;
            
            /* 
             * MATCH - TCP_SRC
             */
         } else if (key.equals("tp_src")) {
             
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_tcp_src");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("tlv", getUint16Fixed(U16.t(Integer.valueOf((String) args.get(key)))));

            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);

            if (this.isIpProto)
               matches.add(oxmTlvRecord);
            else 
               this.delayedTcpSrc = oxmTlvRecord;

            /* 
             * MATCH - TCP_DST
             */
         } else if (key.equals("tp_dst")) {
             
            Schema oxmTlvFieldSchema = protocol.getType("of.oxm_tlv_tcp_dst");
            GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);
             
            oxmTlvFieldRecord.put("tlv", getUint16Fixed(U16.t(Integer.valueOf((String) args.get(key)))));

            Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
            GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
            oxmTlvRecord.put("match", oxmTlvFieldRecord);

            if (this.isIpProto)
               matches.add(oxmTlvRecord);

            else 
               this.delayedTcpDst = oxmTlvRecord;

            /* 
             * MATCH - UDP_SRC
             */
         } else if (key.equals("udp_src")) {

            if (this.isIpProto)
               matches.add(getMatch("of.oxm_tlv_udp_src", getUint16Fixed(U16.t(Integer.valueOf((String) args.get(key))))));
            else 
               this.delayedUdpSrc = getMatch("of.oxm_tlv_udp_src", getUint16Fixed(U16.t(Integer.valueOf((String) args.get(key)))));

            /* 
             * MATCH - UDP_DST
             */
         } else if (key.equals("udp_dst")) {

            if (this.isIpProto)
               matches.add(getMatch("of.oxm_tlv_udp_dst", getUint16Fixed(U16.t(Integer.valueOf((String) args.get(key))))));
            else 
               this.delayedUdpDst = getMatch("of.oxm_tlv_udp_dst", getUint16Fixed(U16.t(Integer.valueOf((String) args.get(key)))));
            
            
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
      if (! isDelete)
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
              String action = subaction.split("[=:]")[0].trim();
              
              GenericRecord ofpActionRecord = null;
              
              if (action.equals("output")) {
                 ofpActionRecord = decode_output(subaction.trim());
              } else if (action.equals("set_field_eth_dst")) {
                 ofpActionRecord = decode_set_field_eth_dst(subaction.trim());
              }  else if (action.equals("set_field_eth_src")) {
                 ofpActionRecord = decode_set_field_eth_src(subaction.trim());
              }  else if (action.equals("set_field_vlan_vid")) {
                 ofpActionRecord = decode_set_field_vlan_vid(subaction.trim());
              }  else if (action.equals("set_field_mpls_label")) {
                 ofpActionRecord = decode_set_field_mpls_label(subaction.trim());
              }  else if (action.equals("pop_vlan")) {
                 ofpActionRecord = decode_pop_vlan(subaction.trim());
              }  else if (action.equals("push_mpls")) {
                 ofpActionRecord = decode_push_mpls(subaction.trim());
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

//          int port = OFPort.OFPP_NONE.getValue();
         int port = 0;
         if (n.group(1) != null) {
              try {
                  port = get_int(n.group(1));
              }
              catch (NumberFormatException e) {
                 // log.debug("  Invalid port in: '{}' (error ignored)", subaction);
                  return null;
              }
          }
/*          else if (n.group(2) != null)
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
              port = OFPort.OFPP_FLOOD.getValue();*/
 
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
          
/*          Schema oxmTlvEthDstSchema = protocol.getType("of.oxm_tlv_eth_dst");
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
          
          matches.add(oxmTlvRecord);*/

          Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
          matches.add(getMatch("of.oxm_tlv_eth_dst", getUint48Fixed(eth)));
          
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
          
/*          Schema oxmTlvEthSrcSchema = protocol.getType("of.oxm_tlv_eth_src");
          GenericRecord oxmTlvEthSrcRecord = new GenericData.Record(oxmTlvEthSrcSchema);
          
          int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                             (OXMField.OFPXMT_OFB_ETH_SRC.getValue() << 9) | 
                             (0 << 8) |
                             6;
          
          oxmTlvEthSrcRecord.put("header", getUint32Fixed(oxmTlvHeader));
          oxmTlvEthSrcRecord.put("tlv", getUint48Fixed(eth));
          
          Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
          GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
          oxmTlvRecord.put("match", oxmTlvEthSrcRecord);*/

          Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");          
          matches.add(getMatch("of.oxm_tlv_eth_src", getUint48Fixed(eth)));
          
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
          
/*          Schema oxmTlvVlanVidSchema = protocol.getType("of.oxm_tlv_vlan_vid");
          GenericRecord oxmTlvVlanVidRecord = new GenericData.Record(oxmTlvVlanVidSchema);
          
          int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                             (OXMField.OFPXMT_OFB_VLAN_VID.getValue() << 9) | 
                             (0 << 8) |
                             2;
          
          oxmTlvVlanVidRecord.put("header", getUint32Fixed(oxmTlvHeader));
          oxmTlvVlanVidRecord.put("tlv", getUint16Fixed(vid));
          
          Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
          GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
          oxmTlvRecord.put("match", oxmTlvVlanVidRecord);*/
          
          Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");          
          matches.add(getMatch("of.oxm_tlv_vlan_vid", getUint16Fixed(vid)));
          
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
         
/*         Schema oxmTlvMplsLabelSchema = protocol.getType("of.oxm_tlv_mpls_label");
         GenericRecord oxmTlvMplsLabelRecord = new GenericData.Record(oxmTlvMplsLabelSchema);
         
         int oxmTlvHeader = (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | 
                            (OXMField.OFPXMT_OFB_MPLS_LABEL.getValue() << 9) | 
                            (0 << 8) |
                            3;
         
         oxmTlvMplsLabelRecord.put("header", getUint32Fixed(oxmTlvHeader));
         oxmTlvMplsLabelRecord.put("tlv", getUint24Fixed(label));
         
         Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
         GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
         oxmTlvRecord.put("match", oxmTlvMplsLabelRecord);*/
         
         Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");         
         matches.add(getMatch("of.oxm_tlv_mpls_label", getUint32Fixed(label)));
         
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
   
   private static Long get_long(byte[] buffer) {
      long result = 0;
      for (int i=0; i<8; i++) 
         result = (result | buffer[i]) << 8; 
      
      return new Long(result);
   }
   
   private static Long get_long(GenericData.Fixed in) {
      long result = 0;
      byte [] buffer = in.bytes();
      
      result |=  ((long)(buffer[7])  & 255);
      result |=  (((long)(buffer[6])  & 255) << 8);
      result |=  (((long)(buffer[5])  & 255) << 16);
      result |=  (((long)(buffer[4])  & 255) << 24);
      result |=  (((long)(buffer[3])  & 255) << 32);
      result |=  (((long)(buffer[2])  & 255) << 40);
      result |=  (((long)(buffer[1])  & 255) << 48);
      result |=  (((long)(buffer[0])  & 255) << 56);
 
      return new Long(result);
   }

   private static Byte get_byte(GenericData.Fixed in) {
      return new Byte(in.bytes()[0]);
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
   
   private GenericData.Fixed getPad3 (int in) {
      // TODO Implement based on pad_2 schema    
      return getUint24Fixed(in);
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
  
  private GenericRecord getMatch (String schemaName, GenericFixed tlv) {
     
     Schema oxmTlvFieldSchema = protocol.getType(schemaName);
     GenericRecord oxmTlvFieldRecord = new GenericData.Record(oxmTlvFieldSchema);

     oxmTlvFieldRecord.put("tlv", tlv);
     
     Schema oxmTlvSchema = protocol.getType("of.oxm_tlv");
     GenericRecord oxmTlvRecord = new GenericData.Record(oxmTlvSchema);
     oxmTlvRecord.put("match", oxmTlvFieldRecord);
     
     return oxmTlvRecord;
     
  }
  
  private short calculateLength (Schema schema, GenericRecord record) {
     
     ByteArrayOutputStream buf = new ByteArrayOutputStream();
     
     DatumWriter<GenericRecord> instrWriter = new GenericDatumWriter<GenericRecord>(schema);
     Encoder encoder = EncoderFactory.get().binaryNonEncoder(buf, null);
    
     try {
        instrWriter.write(record, encoder);
        encoder.flush();
        
     } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
     }
     
     return (short)buf.size();
  }

/* (non-Javadoc)
 * @see org.flowforwarding.of.controller.protocol.OFMessageProvider#isHello(byte[])
 */
@Override
public boolean isHello(byte[] in) {
   return true;
}

/* (non-Javadoc)
 * @see org.flowforwarding.of.controller.protocol.OFMessageProvider#getVersion()
 */
@Override
public Short getVersion() {
   return null;
}

/* (non-Javadoc)
 * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isFeautureReply(byte[])
 */
@Override
public boolean isFeautureReply(byte[] in) {
   
   return false;
}

/* (non-Javadoc)
 * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isConfig(byte[])
 */
@Override
public boolean isConfig(byte[] in) {
   GenericRecord record = getSwitchCofigRecord(in);
   GenericRecord header = new GenericData.Record(ofpHeaderSchema);
   
   header = (GenericRecord) record.get("header");
   // TODO Improvs: We plan to get all types from Avro protocol type... soon... so let it be now just 8
   Byte type = get_byte((GenericData.Fixed)header.get("type")); 
   if (type.byteValue() == 8 )  // OFPT_GET_CONFIG_REPLY
      return true;
   else 
      return false;
}
  
}
