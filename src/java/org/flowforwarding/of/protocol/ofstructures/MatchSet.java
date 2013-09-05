/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofstructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Infoblox Inc.
 *
 */
public class MatchSet {

   protected enum MatchFields {
      OFPXMT_OFB_IN_PORT,
      OFPXMT_OFB_IN_PHY_PORT,
      OFPXMT_OFB_METADATA,
      OFPXMT_OFB_ETH_DST,
      OFPXMT_OFB_ETH_SRC,
      OFPXMT_OFB_ETH_TYPE,
      OFPXMT_OFB_VLAN_VID,
      OFPXMT_OFB_VLAN_PCP,
      OFPXMT_OFB_IP_DSCP,
      OFPXMT_OFB_IP_ECN,
      OFPXMT_OFB_IP_PROTO,
      OFPXMT_OFB_IPV4_SRC,
      OFPXMT_OFB_IPV4_DST,
      OFPXMT_OFB_TCP_SRC,
      OFPXMT_OFB_TCP_DST,
      OFPXMT_OFB_UDP_SRC,
      OFPXMT_OFB_UDP_DST,
      OFPXMT_OFB_SCTP_SRC,
      OFPXMT_OFB_SCTP_DST,
      OFPXMT_OFB_ICMPV4_TYPE,
      OFPXMT_OFB_ICMPV4_CODE,
      OFPXMT_OFB_ARP_OP,
      OFPXMT_OFB_ARP_SPA,
      OFPXMT_OFB_ARP_TPA,
      OFPXMT_OFB_ARP_SHA,
      OFPXMT_OFB_ARP_THA,
      OFPXMT_OFB_IPV6_SRC,
      OFPXMT_OFB_IPV6_DST,
      OFPXMT_OFB_IPV6_FLABEL,
      OFPXMT_OFB_ICMPV6_TYPE,
      OFPXMT_OFB_ICMPV6_CODE,
      OFPXMT_OFB_IPV6_ND_TARGET,
      OFPXMT_OFB_IPV6_ND_SLL,
      OFPXMT_OFB_IPV6_ND_TLL,
      OFPXMT_OFB_MPLS_LABEL,
      OFPXMT_OFB_MPLS_TC,
      OFPXMT_OFP_MPLS_BOS,
      OFPXMT_OFB_PBB_ISID,
      OFPXMT_OFB_TUNNEL_ID,
      OFPXMT_OFB_IPV6_EXTHDR 
   }
   
   protected List<Tuple<String, String>> matchSet = new ArrayList<>();
   protected Iterator<Tuple<String, String>> iter = matchSet.iterator();
   
   // TODO Improvements: Is this ok solution to return the List? Think about Avro API and AKKA Api decoupling in general.
   // TODO Improvements: Use this class as a Wrapper around the List: implement next(), hasNext() etc.
   public List<Tuple<String, String>> getMatches() {
      return matchSet;
   }
   
   public Iterator<Tuple<String, String>> getIterator () {
      return iter;
   }
   
   public void add (String name, String value) {
      matchSet.add(new Tuple<String, String>(name, value));
   }
}
