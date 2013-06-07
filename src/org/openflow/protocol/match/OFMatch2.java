package org.openflow.protocol.match;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.match.OFMatchType.*;
import org.openflow.protocol.serializers.OFMatchJSONSerializer;
import org.openflow.util.HexString;
import org.openflow.util.U16;
import org.openflow.util.U8;

/**
 * Represents an ofp_match structure
 * 
 * @author David Erickson (daviderickson@cs.stanford.edu)
 * @author Rob Sherwood (rob.sherwood@stanford.edu)
 * 
 */
@JsonSerialize(using=OFMatchJSONSerializer.class)
public class OFMatch2 implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3742169407930572410L;
	/**
     * OpenFlow Version 1.2
     * BEGIN
     * TYPES
     */
	public static short MINIMUM_LENGTH = 8;
	
	protected OFPMatchType type; 	/* One of OFPMT_* */
	protected short length; 		/* Length of ofp_match (excluding padding) */
					/* Followed by:
					 * -Exactly(length-4)(possibly 0) bytes containing OXM TLVs, then
					 * -Exactly((length+7)/8*8-length) (between 0 and 7) bytes of
					 * all-zero bytes
					 * In summary, ofp_match is padded as needed, to make its overall size
					 * a multiple of 8, to preserve alignement in structures using it.
					 */
							
    //protected byte oxmFields[] = {0x0, 0x0, 0x0, 0x0}; /* OXMs start here - Make compiler happy */
    protected byte pad[] = {0x0, 0x0, 0x0, 0x0}; /* OXMs start here - Make compiler happy */
    // TODO DO - Think about explicit constructor call for this?
    protected List<OFMatchOXM> oxmTLVs;
	protected short closingPad = 0;
	
	public short closeMatch2() {
	//	if (this.length > 8)
		closingPad = (short) ((length + 7)/8*8 - length);
		
		return closingPad;
	}

    /* List of Strings for marshalling and unmarshalling to human readable forms */
    final public static String STR_IN_PORT = "in_port";
    final public static String STR_DL_DST = "dl_dst";
    final public static String STR_DL_SRC = "dl_src";
    final public static String STR_DL_TYPE = "dl_type";
    final public static String STR_DL_VLAN = "dl_vlan";
    final public static String STR_DL_VLAN_PCP = "dl_vlan_pcp";
    final public static String STR_NW_DST = "nw_dst";
    final public static String STR_NW_SRC = "nw_src";
    final public static String STR_NW_PROTO = "nw_proto";
    final public static String STR_NW_TOS = "nw_tos";
    final public static String STR_TP_DST = "tp_dst";
    final public static String STR_TP_SRC = "tp_src";
    final public static String STR_UDP_DST = "udp_dst";
    final public static String STR_UDP_SRC = "udp_src";

    

	public OFMatch2() {
		this.setType(OFPMatchType.OFPMT_OXM);
		this.setLength((short)(MINIMUM_LENGTH - 4));
		
		this.oxmTLVs = new LinkedList<OFMatchOXM>();
	}
	
    public void writeTo(ChannelBuffer data) {
    	data.writeShort(this.type.getValue());
    	data.writeShort(this.length);
    	
		for (OFMatchOXM oxmTLV : oxmTLVs) {
			oxmTLV.writeTo(data);
    	}
    	
    	for (int i = 0; i < closingPad; i++) {
    		data.writeByte(0);
    	}
    	
    	/*if (this.length == MINIMUM_LENGTH - 4)
    		data.writeBytes(this.pad);
    	else 
    		for (OFMatchOXM oxmTLV : oxmTLVs) {
    			oxmTLV.writeTo(data);
        	}
    	
    	for (int i=0; i < this.closingPad; i++) {
    		data.writeByte(0);
    	}*/
    }
    
    public void addOXMTlv (OFMatchOXM oxmTLV) {
    	this.oxmTLVs.add(oxmTLV);
    	
/*    	if (this.length == 4) 
    		this.length += oxmTLV.getLength();
    	else
    		this.length += oxmTLV.getLength();*/
    	
    	this.length += (oxmTLV.getLength() + 4);
    }
    
    /**
	 * @return the type
	 */
	public OFPMatchType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(OFPMatchType type) {
		this.type = type;
	}

	/**
	 * @return the length
	 */
	public short getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(short length) {
		this.length = length;
	}
	
    @Override
    public OFMatch2 clone() {
    	// TODO DO - OFMatch2.clone()!!!! add match list creating see OFFlowMod clone
        try {
            OFMatch2 ret = (OFMatch2) super.clone();
            return ret;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Set this OFMatch's parameters based on a comma-separated key=value pair
     * dpctl-style string, e.g., from the output of OFMatch.toString() <br>
     * <p>
     * Supported keys/values include <br>
     * <p>
     * <TABLE border=1>
     * <TR>
     * <TD>KEY(s)
     * <TD>VALUE
     * </TR>
     * <TR>
     * <TD>"in_port","input_port"
     * <TD>integer
     * </TR>
     * <TR>
     * <TD>"dl_src","eth_src", "dl_dst","eth_dst"
     * <TD>hex-string
     * </TR>
     * <TR>
     * <TD>"dl_type", "dl_vlan", "dl_vlan_pcp"
     * <TD>integer
     * </TR>
     * <TR>
     * <TD>"nw_src", "nw_dst", "ip_src", "ip_dst"
     * <TD>CIDR-style netmask
     * </TR>
     * <TR>
     * <TD>"tp_src","tp_dst"
     * <TD>integer (max 64k)
     * </TR>
     * </TABLE>
     * <p>
     * The CIDR-style netmasks assume 32 netmask if none given, so:
     * "128.8.128.118/32" is the same as "128.8.128.118"
     * 
     * @param match
     *            a key=value comma separated string, e.g.
     *            "in_port=5,ip_dst=192.168.0.0/16,tp_src=80"
     * @throws IllegalArgumentException
     *             on unexpected key or value
     */

    public void fromString(String match) throws IllegalArgumentException {
        if (match.equals("") || match.equalsIgnoreCase("any")
                || match.equalsIgnoreCase("all") || match.equals("[]"))
            match = "OFMatch[]";
        String[] tokens = match.split("[\\[,\\]]");
        String[] values;
        int initArg = 0;
        if (tokens[0].equals("OFMatch"))
            initArg = 1;
//        this.wildcards = OFPFW_ALL;
        int i;
        for (i = initArg; i < tokens.length; i++) {
            values = tokens[i].split("=");
            if (values.length != 2)
                throw new IllegalArgumentException("Token " + tokens[i]
                        + " does not have form 'key=value' parsing " + match);
            values[0] = values[0].toLowerCase(); // try to make this case insens
            if (values[0].equals(STR_IN_PORT) || values[0].equals("input_port")) {
//                this.inputPort = U16.t(Integer.valueOf(values[1]));
            	OFMatchOXM oxmTLVIngressPort = new OFMatchIngressPort(U16.t(Integer.valueOf(values[1])));
            	this.addOXMTlv(oxmTLVIngressPort);
            } else if (values[0].equals(STR_DL_DST) || values[0].equals("eth_dst")) {
//                this.dataLayerDestination = HexString.fromHexString(values[1]);
//                this.wildcards &= ~OFPFW_DL_DST;
            } else if (values[0].equals(STR_DL_SRC) || values[0].equals("eth_src")) {
//                this.dataLayerSource = HexString.fromHexString(values[1]);
//                this.wildcards &= ~OFPFW_DL_SRC;
            } else if (values[0].equals(STR_DL_TYPE) || values[0].equals("eth_type")) {
            	OFMatchOXM oxmTLVEthType = new OFMatchETHType(U16.t(Integer.valueOf(values[1].replaceFirst("0x", ""), 16)));
            	this.addOXMTlv(oxmTLVEthType);
/*                if (values[1].startsWith("0x"))
                    this.dataLayerType = U16.t(Integer.valueOf(
                            values[1].replaceFirst("0x", ""), 16));
                else
                    this.dataLayerType = U16.t(Integer.valueOf(values[1]));
                this.wildcards &= ~OFPFW_DL_TYPE;*/
            } else if (values[0].equals(STR_DL_VLAN)) {
/*            	if (values[1].startsWith("0x"))
            		this.dataLayerVirtualLan = U16.t(Integer.valueOf(
            				values[1].replaceFirst("0x", ""),16));
            	else
            		this.dataLayerVirtualLan = U16.t(Integer.valueOf(values[1]));
                this.wildcards &= ~OFPFW_DL_VLAN;*/
            } else if (values[0].equals(STR_DL_VLAN_PCP)) {
/*                this.dataLayerVirtualLanPriorityCodePoint = U8.t(Short
                        .valueOf(values[1]));
                this.wildcards &= ~OFPFW_DL_VLAN_PCP; */
            } else if (values[0].equals(STR_NW_DST) || values[0].equals("ip_dst")) {
            	  OFMatchOXM oxmTLVIp4src = new OFMatchIP4Dst(ipFromCIDR(values[1], STR_NW_DST));  // OF 1.2
            	  this.addOXMTlv(oxmTLVIp4src);
//                setFromCIDR(values[1], STR_NW_DST);
            } else if (values[0].equals(STR_NW_SRC) || values[0].equals("ip_src")) {
          	      OFMatchOXM oxmTLVIp4dst = new OFMatchIP4Src(ipFromCIDR(values[1], STR_NW_SRC));  // OF 1.2
          	      this.addOXMTlv(oxmTLVIp4dst);
//                setFromCIDR(values[1], STR_NW_SRC);
            } else if (values[0].equals(STR_NW_PROTO)) {
/*                this.networkProtocol = U8.t(Short.valueOf(values[1]));
                this.wildcards &= ~OFPFW_NW_PROTO; */
            } else if (values[0].equals(STR_NW_TOS)) {
/*                this.setNetworkTypeOfService(U8.t(Short.valueOf(values[1])));
                this.wildcards &= ~OFPFW_NW_TOS; */
            } else if (values[0].equals(STR_TP_DST)) {
          	      OFMatchOXM oxmTLVTcpDst = new OFMatchTCPDst(U16.t(Integer.valueOf(values[1])));  // OF 1.2
          	      this.addOXMTlv(oxmTLVTcpDst);
/*                this.transportDestination = U16.t(Integer.valueOf(values[1]));
                this.wildcards &= ~OFPFW_TP_DST; */
            } else if (values[0].equals(STR_TP_SRC)) {
        	      OFMatchOXM oxmTLVTcpSrc = new OFMatchTCPSrc(U16.t(Integer.valueOf(values[1])));  // OF 1.2
        	      this.addOXMTlv(oxmTLVTcpSrc);            	
/*                this.transportSource = U16.t(Integer.valueOf(values[1]));
                this.wildcards &= ~OFPFW_TP_SRC; */
            } else if (values[0].equals(STR_UDP_DST)) {
        	      OFMatchOXM oxmTLVUdpDst = new OFMatchUDPDst(U16.t(Integer.valueOf(values[1])));  // OF 1.2
        	      this.addOXMTlv(oxmTLVUdpDst);
/*                this.transportDestination = U16.t(Integer.valueOf(values[1]));
              this.wildcards &= ~OFPFW_TP_DST; */
           } else if (values[0].equals(STR_UDP_SRC)) {
      	      OFMatchOXM oxmTLVUdpSrc = new OFMatchUDPSrc(U16.t(Integer.valueOf(values[1])));  // OF 1.2
      	      this.addOXMTlv(oxmTLVUdpSrc);            	
/*                this.transportSource = U16.t(Integer.valueOf(values[1]));
              this.wildcards &= ~OFPFW_TP_SRC; */        	      
            } else {
                throw new IllegalArgumentException("unknown token " + tokens[i]
                        + " parsing " + match);
            }
        }
    }

  
    /**
     * Set the networkSource or networkDestionation address and their wildcards
     * from the CIDR string
     * 
     * @param cidr
     *            "192.168.0.0/16" or "172.16.1.5"
     * @param which
     *            one of STR_NW_DST or STR_NW_SRC
     * @throws IllegalArgumentException
     */
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
/*        if (which.equals(STR_NW_DST)) {
            this.networkDestination = ip;
            this.wildcards = (wildcards & ~OFPFW_NW_DST_MASK)
                    | (mask << OFPFW_NW_DST_SHIFT);
        } else if (which.equals(STR_NW_SRC)) {
            this.networkSource = ip;
            this.wildcards = (wildcards & ~OFPFW_NW_SRC_MASK)
                    | (mask << OFPFW_NW_SRC_SHIFT);
        }*/
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + Arrays.hashCode(pad);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OFMatch2 other = (OFMatch2) obj;
		if (length != other.length)
			return false;
		if (!Arrays.equals(pad, other.pad))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public void readFrom(ChannelBuffer data) {
		// TODO Auto-generated method stub
		
	}

}