package org.openflow.protocol.match;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.match.OFMatchType.*;
import org.openflow.util.U16;

public class OFMatchUDPDst  extends OFMatchOXM implements Cloneable {
	
	public static short MINIMUM_LENGTH = 6;
	public static short OXM_LENGTH = 2;
	
    protected static int OXM_HEADER_UDP_DST = (int) ( (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | OXMField.OFPXMT_OFB_UDP_DST.getValue() << 9 | 0 << 8 | OXM_LENGTH);
    
	/*protected OXMClass oxmClass;
	protected OXMField field;
	protected HasMask hasMask;
	protected int length;*/
	
	protected short oxmTLV;
	//protected short length;
	
	public short getLength() {
		return length;
	}
	
	public OFMatchUDPDst(short oxmTLV) {
		super(OXM_HEADER_UDP_DST);
		this.oxmTLV = oxmTLV;
		this.length = OXM_LENGTH;		
	}
	
	public OFMatchUDPDst() {
		super(OXM_HEADER_UDP_DST);
		this.oxmTLV = 0xFFFFFFFF;
		this.length = OXM_LENGTH;
	}
	
    public void writeTo(ChannelBuffer data) {
    	//super.writeTo(data);
    	
    	data.writeInt(oxmHeader);
    	data.writeShort(U16.f(oxmTLV));
    }

}
