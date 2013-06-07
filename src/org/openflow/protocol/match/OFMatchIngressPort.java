package org.openflow.protocol.match;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.match.OFMatchType.*;

public class OFMatchIngressPort extends OFMatchOXM implements Cloneable {

	public static short MINIMUM_LENGTH = 8;
	public static short OXM_LENGTH = 4;
	
    protected static int OXM_HEADER_INGRESS_PORT = (int) ( (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | OXMField.OFPXMT_OFB_IN_PORT.getValue() << 9 | 0 << 8 | OXM_LENGTH);
    
	/*protected OXMClass oxmClass;
	protected OXMField field;
	protected HasMask hasMask;
	protected int length;*/
	
	protected int oxmTLV;
//	protected short length;
	
	public short getLength() {
		return length;
	}
	
	public OFMatchIngressPort(int oxmTLV) {
		super(OXM_HEADER_INGRESS_PORT);
		this.oxmTLV = oxmTLV;
		this.length = OXM_LENGTH;		
	}
	
	public OFMatchIngressPort() {
		super(OXM_HEADER_INGRESS_PORT);
		this.oxmTLV = 0xFFFFFFFF;
		this.length = OXM_LENGTH;
	}
	
	
	
    public void writeTo(ChannelBuffer data) {
    	//super.writeTo(data);
    	
    	data.writeInt(oxmHeader);
    	data.writeInt(oxmTLV);
    }	
}
