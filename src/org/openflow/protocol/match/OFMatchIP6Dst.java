package org.openflow.protocol.match;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.match.OFMatchType.*;

public class OFMatchIP6Dst extends OFMatchOXM implements Cloneable {

   public static short MINIMUM_LENGTH = 8;
   public static short OXM_LENGTH = 4;
   
    protected static int OXM_HEADER_IP6_DST = (int) ( (OXMClass.OFPXMC_OPENFLOW_BASIC.getValue() << 16) | OXMField.OFPXMT_OFB_IPV6_DST.getValue() << 9 | 0 << 8 | OXM_LENGTH);
    
   /*protected OXMClass oxmClass;
   protected OXMField field;
   protected HasMask hasMask;
   protected int length;*/
   
   protected int oxmTLV;
   //protected short length;
   
   public short getLength() {
      return length;
   }
   
   public OFMatchIP6Dst(int oxmTLV) {
      super(OXM_HEADER_IP6_DST);
      this.oxmTLV = oxmTLV;
      this.length = OXM_LENGTH;     
   }
   
   public OFMatchIP6Dst() {
      super(OXM_HEADER_IP6_DST);
      this.oxmTLV = 0xFFFFFFFF;
      this.length = OXM_LENGTH;
   }
   
    public void writeTo(ChannelBuffer data) {
      //super.writeTo(data);
      
      data.writeInt(oxmHeader);
      data.writeInt(oxmTLV);
    } 
}
