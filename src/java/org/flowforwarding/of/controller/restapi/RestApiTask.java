package org.flowforwarding.of.controller.restapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;

//public class RestApiTask extends RecursiveTask <Map<String, Map<String, OFFlowMod>>>
public class RestApiTask extends RecursiveTask <Map<String, Object>>
{
   
   /**
    * 
    */
   private static final long serialVersionUID = 2872160241865695301L;

   
   public RestApiTask(String jsonRequest,
          Map<String, Object> entries) {
           // Map<String, Map<String, OFFlowMod>> entries) {
      super();
      this.jsonRequest = jsonRequest;
      this.entries = entries;
   }

   private String jsonRequest;
  // private Map<String, Map<String, OFFlowMod>> entries;
   private Map<String, Object> entries;
   
   public static final String TABLE_NAME = "controller_staticflowtableentry";
   public static final String COLUMN_NAME = "name";
   public static final String COLUMN_SWITCH = "switch_id";
   public static final String COLUMN_ACTIVE = "active";
   public static final String COLUMN_IDLE_TIMEOUT = "idle_timeout";
   public static final String COLUMN_HARD_TIMEOUT = "hard_timeout";
   public static final String COLUMN_PRIORITY = "priority";
   public static final String COLUMN_COOKIE = "cookie";
   public static final String COLUMN_WILDCARD = "wildcards";
   public static final String COLUMN_IN_PORT = "in_port";                        //0
   public static final String COLUMN_IN_PHY_PORT = "in_phy_port";                //1
   public static final String COLUMN_METADATA = "metadata";                      //2
   public static final String COLUMN_DL_SRC = "dl_src";                          //3
   public static final String COLUMN_DL_DST = "dl_dst";                          //4
   public static final String COLUMN_DL_TYPE = "dl_type";                        //5
   public static final String COLUMN_DL_VLAN = "vlan_vid";                       //6
   public static final String COLUMN_DL_VLAN_PCP = "dl_vlan_pcp";                //7
   public static final String COLUMN_IP_DSCP = "ip_dscp";                        //8
   public static final String COLUMN_IP_ECN = "ip_ecn";                          //9
   public static final String COLUMN_NW_PROTO = "nw_proto";                      //10
   public static final String COLUMN_NW_SRC = "nw_src"; // includes CIDR-style
                                                        // netmask, e.g.
                                                        // "128.8.128.0/24"
   public static final String COLUMN_NW_DST = "nw_dst";                          //12
   public static final String COLUMN_TP_SRC = "tp_src";                          //13
   public static final String COLUMN_TP_DST = "tp_dst";                          //14
   public static final String COLUMN_UDP_DST = "udp_dst";                        //15
   public static final String COLUMN_UDP_SRC = "udp_src";                        //16
   public static final String COLUMN_SCTP_SRC = "sctp_src";                      //17
   public static final String COLUMN_SCTP_DST = "sctp_dst";                      //18
   public static final String COLUMN_ICMPV4_TYPE = "icmpv4_type";                //19
   public static final String COLUMN_ICMPV4_CODE = "icmpv4_code";                //20
   public static final String COLUMN_ARP_OP = "arp_op";                          //21
   public static final String COLUMN_ARP_SPA = "arp_spa";                        //22
   public static final String COLUMN_ARP_TPA = "arp_tpa";                        //23
   public static final String COLUMN_ARP_SHA = "arp_sha";                        //24
   public static final String COLUMN_ARP_THA = "arp_tha";                        //25
   public static final String COLUMN_IPV6_SRC = "ipv6_src";                      //26
   public static final String COLUMN_IPV6_DST = "ipv6_dst";                      //27
   public static final String COLUMN_IPV6_FLABEL = "ipv6_flabel";                //28   
   public static final String COLUMN_ICMPV6_TYPE = "icmpv6_type";                //29
   public static final String COLUMN_ICMPV6_CODE = "icmpv6_code";                //30
   public static final String COLUMN_IPV6_ND_TARGET = "ipv6_nd_target";          //31
   public static final String COLUMN_IPV6_ND_SLL = "ipv6_nd_sll";                //32
   public static final String COLUMN_IPV6_ND_TLL = "ipv6_nd_tll";                //33
   public static final String COLUMN_MPLS_LABEL = "mpls_label";                  //34
   public static final String COLUMN_MPLS_TC = "mpls_tc";                        //35
   public static final String COLUMN_MPLS_BOS = "mpls_bos";                      //36
   public static final String COLUMN_PBB_ISID = "pbb_isid";                      //37
   public static final String COLUMN_TUNNEL_ID = "tunnel_id";                    //38
   public static final String COLUMN_IPV6_EXTHDR = "ipv6_exthdr";                //39

   
   public static final String COLUMN_NW_TOS = "nw_tos";
   public static final String COLUMN_WRITE_METADATA = "write_metadata";
   public static final String COLUMN_GOTO_TABLE = "goto_table";
   public static final String COLUMN_METER = "meter";
   public static final String COLUMN_APPLY_ACTIONS = "apply_actions";
   public static final String COLUMN_WRITE_ACTIONS = "write_actions";
   public static final String COLUMN_CLEAR_ACTIONS = "clear_actions";
   
   public static String ColumnNames[] = { COLUMN_NAME, COLUMN_SWITCH,
           COLUMN_ACTIVE, COLUMN_IDLE_TIMEOUT, COLUMN_HARD_TIMEOUT,
           COLUMN_PRIORITY, COLUMN_COOKIE, COLUMN_WILDCARD, COLUMN_IN_PORT, COLUMN_IN_PHY_PORT,
           COLUMN_METADATA, COLUMN_DL_SRC, COLUMN_DL_DST, COLUMN_DL_TYPE, COLUMN_DL_VLAN,
           COLUMN_DL_VLAN_PCP, COLUMN_IP_DSCP, COLUMN_IP_ECN, COLUMN_NW_PROTO, COLUMN_NW_SRC,
           COLUMN_NW_DST, COLUMN_TP_DST, COLUMN_TP_SRC, COLUMN_UDP_DST, COLUMN_UDP_SRC,
           COLUMN_SCTP_SRC, COLUMN_SCTP_DST, COLUMN_ICMPV4_TYPE, COLUMN_ICMPV4_CODE, COLUMN_ARP_OP,
           COLUMN_ARP_SPA, COLUMN_ARP_TPA, COLUMN_ARP_SHA, COLUMN_ARP_THA, COLUMN_IPV6_SRC, 
           COLUMN_IPV6_DST, COLUMN_IPV6_FLABEL, COLUMN_ICMPV6_TYPE, COLUMN_ICMPV6_CODE, COLUMN_IPV6_ND_TARGET,
           COLUMN_IPV6_ND_SLL, COLUMN_IPV6_ND_TLL, COLUMN_MPLS_LABEL, COLUMN_MPLS_TC, COLUMN_MPLS_BOS,
           COLUMN_PBB_ISID, COLUMN_TUNNEL_ID, COLUMN_IPV6_EXTHDR, COLUMN_APPLY_ACTIONS, COLUMN_WRITE_ACTIONS, 
           COLUMN_CLEAR_ACTIONS, COLUMN_WRITE_METADATA, COLUMN_GOTO_TABLE, COLUMN_METER};
   
   @Override
   //protected Map<String, Map<String, OFFlowMod>> compute() {
   protected Map<String, Object> compute() {
   
      Map<String, Object> rowValues = null;
      
      try {
         rowValues = jsonToStorageEntry(jsonRequest);
         //parseRow(rowValues, entries);
         
         
         /*Iterator<Map.Entry<String, Object>> it = rowValues.entrySet().iterator();
         while (it.hasNext()) {
             Map.Entry<String, Object> entry = it.next();
             System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
         }*/
         
/*         for (Iterator<Map<String, Object>> it = rowValues.i; it.hasNext();) {
            Map<String, Object> row = it.next().getRow();
            parseRow(row, entries); 
        }   */
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
/*      Map<String, Object> entry = new HashMap<String, Object>();
      MappingJsonFactory f = new MappingJsonFactory();
      JsonParser jp;

      try {
         jp = f.createJsonParser(jsonRequest);
         
         jp.nextToken();
         
         if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected START_OBJECT");
         }
         
         while (jp.nextToken() != JsonToken.END_OBJECT) {
            if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
               throw new IOException("Expected FIELD_NAME");
            }
            String n = jp.getCurrentName();
            jp.nextToken();
            
            if (jp.getText().equals(""))
               continue;
            
            if (n == "switch") {
               String v = jp.getText();
            }
            
            continue;
         }
            
         if (ofMessageFactory == null) // lazy init
            ofMessageFactory = new BasicFactory();

         OFFlowMod flowMod = (OFFlowMod) ofMessageFactory
                .getMessage(OFType.FLOW_MOD);
         
         } catch (JsonParseException e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      
  */
      
      //return entries;
      return rowValues;
   }
   
   public static Map<String, Object> jsonToStorageEntry(String fmJson) throws IOException {
      Map<String, Object> entry = new HashMap<String, Object>();
      MappingJsonFactory f = new MappingJsonFactory();
      JsonParser jp;
      
      try {
          jp = f.createJsonParser(fmJson);
      } catch (JsonParseException e) {
          throw new IOException(e);
      }
      
      jp.nextToken();
      if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
          throw new IOException("Expected START_OBJECT");
      }
      
      while (jp.nextToken() != JsonToken.END_OBJECT) {
          if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
              throw new IOException("Expected FIELD_NAME");
          }
          
          String n = jp.getCurrentName();
          jp.nextToken();
          if (jp.getText().equals("")) 
              continue;
          
          if (n == "name")
              entry.put(COLUMN_NAME, jp.getText());
          else if (n == "switch")
              entry.put(COLUMN_SWITCH, jp.getText());
          else if (n == "write-actions")
              entry.put(COLUMN_WRITE_ACTIONS, jp.getText());
          else if (n == "apply-actions")
             entry.put(COLUMN_APPLY_ACTIONS, jp.getText());
          else if (n == "clear-actions")
             entry.put(COLUMN_CLEAR_ACTIONS, jp.getText());
          else if (n == "goto-table")
            entry.put(COLUMN_GOTO_TABLE, jp.getText());
          else if (n == "write-metadata")
            entry.put(COLUMN_WRITE_METADATA, jp.getText());
          else if (n == "meter")
             entry.put(COLUMN_METER, jp.getText());
          else if (n == "priority")
              entry.put(COLUMN_PRIORITY, jp.getText());
          else if (n == "active")
              entry.put(COLUMN_ACTIVE, jp.getText());
          else if (n == "wildcards")
              entry.put(COLUMN_WILDCARD, jp.getText());        
          else if (n == "ingress-port")
              entry.put(COLUMN_IN_PORT, jp.getText());         //0
          else if (n == "in-phy-port")
             entry.put(COLUMN_IN_PHY_PORT, jp.getText());      //1
          else if (n == "metadata")
             entry.put(COLUMN_METADATA, jp.getText());         //2
          else if (n == "src-mac")
              entry.put(COLUMN_DL_SRC, jp.getText());          //3
          else if (n == "dst-mac")
              entry.put(COLUMN_DL_DST, jp.getText());          //4
          else if (n == "ether-type")
             entry.put(COLUMN_DL_TYPE, jp.getText());          //5
          else if (n == "vlan-vid")
              entry.put(COLUMN_DL_VLAN, jp.getText());         //6
          else if (n == "vlan-priority")
              entry.put(COLUMN_DL_VLAN_PCP, jp.getText());     //7
          else if (n == "ip-dscp")
             entry.put(COLUMN_IP_DSCP, jp.getText());          //8
          else if (n == "ip-ecn")
             entry.put(COLUMN_IP_ECN, jp.getText());           //9
          else if (n == "protocol")
             entry.put(COLUMN_NW_PROTO, jp.getText());         //10
         else if (n == "src-ip")
             entry.put(COLUMN_NW_SRC, jp.getText());           //11
         else if (n == "dst-ip")
             entry.put(COLUMN_NW_DST, jp.getText());           //12
         else if (n == "src-port")
            entry.put(COLUMN_TP_SRC, jp.getText());            //13
        else if (n == "dst-port")
            entry.put(COLUMN_TP_DST, jp.getText());            //14
        else if (n == "udp-src")
            entry.put(COLUMN_UDP_SRC, jp.getText());           //16
        else if (n == "udp-dst")
            entry.put(COLUMN_UDP_DST, jp.getText());           //17
        else if (n == "sctp-src")
           entry.put(COLUMN_SCTP_SRC, jp.getText());           //18
        else if (n == "sctp-dst")
           entry.put(COLUMN_SCTP_DST, jp.getText());           //19
        else if (n == "icmpv4-type")
           entry.put(COLUMN_ICMPV4_TYPE, jp.getText());        //20
        else if (n == "icmpv4-code")
           entry.put(COLUMN_ICMPV4_CODE, jp.getText());        //21
        else if (n == "arp-op")
           entry.put(COLUMN_ARP_OP, jp.getText());             //22
        else if (n == "arp-spa")
           entry.put(COLUMN_ARP_SPA, jp.getText());            //23
        else if (n == "arp-tpa") 
           entry.put(COLUMN_ARP_TPA, jp.getText());            //24
        else if (n == "arp-sha") 
           entry.put(COLUMN_ARP_SHA, jp.getText());            //25
        else if (n == "arp-tha")
           entry.put(COLUMN_ARP_THA, jp.getText());            //26
        else if (n == "ipv6-src")
           entry.put(COLUMN_IPV6_SRC, jp.getText());           //27
        else if (n == "ipv6-dst")
           entry.put(COLUMN_IPV6_DST, jp.getText());           //28
        else if (n == "ipv6-flabel")
           entry.put(COLUMN_IPV6_FLABEL, jp.getText());        //29
        else if (n == "icmpv6-type")
           entry.put(COLUMN_ICMPV6_TYPE, jp.getText());        //30
        else if (n == "icmpv6-code")
           entry.put(COLUMN_ICMPV6_CODE, jp.getText());        //31
        else if (n == "ipv6-nd-sll")
           entry.put(COLUMN_IPV6_ND_SLL, jp.getText());        //32
        else if (n == "ipv6-nd-tll")
           entry.put(COLUMN_IPV6_ND_TLL, jp.getText());        //33
        else if (n == "mpls-label")
           entry.put(COLUMN_MPLS_LABEL, jp.getText());         //34
        else if (n == "mpls-tc")
           entry.put(COLUMN_MPLS_TC, jp.getText());            //35
        else if (n == "mpls-bos")
           entry.put(COLUMN_MPLS_BOS, jp.getText());           //36
        else if (n == "pbb-isid")
           entry.put(COLUMN_PBB_ISID, jp.getText());           //37
        else if (n == "tunnel-id")
           entry.put(COLUMN_TUNNEL_ID, jp.getText());          //38
        else if (n == "ipv6-exthdr")
           entry.put(COLUMN_IPV6_EXTHDR, jp.getText());        //39
        else if (n == "tos-bits")
           entry.put(COLUMN_NW_TOS, jp.getText());
      }
      
      return entry;
  }
 


  
  private static byte[] get_mac_addr(Matcher n, String subaction/*, Logger log*/) {
     byte[] macaddr = new byte[6];
     
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
     
     return macaddr;
 }
 
 private static int get_ip_addr(Matcher n, String subaction/*, Logger log*/) {
     int ipaddr = 0;

     for (int i=0; i<4; i++) {
         if (n.group(i+1) != null) {
             try {
                 ipaddr = ipaddr<<8;
                 ipaddr = ipaddr | get_int(n.group(i+1));
             }
             catch (NumberFormatException e) {
    //             log.debug("  Invalid src-ip in: '{}' (error ignored)", subaction);
                 return 0;
             }
         }
         else {
    //         log.debug("  Invalid src-ip in: '{}' (null, error ignored)", subaction);
             return 0;
         }
     }
     
     return ipaddr;
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
}
