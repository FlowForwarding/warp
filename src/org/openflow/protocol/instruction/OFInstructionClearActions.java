package org.openflow.protocol.instruction;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;

public class OFInstructionClearActions extends OFInstruction implements Cloneable {
	
	public static int MINIMUM_LENGTH = 8;
	
    protected byte pad[] = {0x0, 0x0, 0x0, 0x0};
    
    public OFInstructionClearActions() {
        super.setType(OFInstructionType.OFPIT_CLEAR_ACTIONS);
        super.setLength((short) MINIMUM_LENGTH);
    }
    
    @Override
    public void readFrom(ChannelBuffer data) {
        super.readFrom(data);
        
        this.pad[0] = data.readByte();
        this.pad[1] = data.readByte();
        this.pad[2] = data.readByte();
        this.pad[3] = data.readByte();
        
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        super.writeTo(data);
        data.writeBytes(this.pad);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(pad);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof OFInstructionClearActions))
			return false;
		OFInstructionClearActions other = (OFInstructionClearActions) obj;
		if (!Arrays.equals(pad, other.pad))
			return false;
		return true;
	}
}
