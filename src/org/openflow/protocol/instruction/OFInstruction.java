package org.openflow.protocol.instruction;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.util.U16;

/**
 * Represents an ofp_instruction structure
 * 
*  @author David Erickson (daviderickson@cs.stanford.edu)
 * @author Dmitry Orekhov (dmitry_orekhov@epam.com)
 * 
 */
// TODO DO - Make the OFInstruction serializable, like OFMatch
public class OFInstruction  implements Cloneable {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	protected OFInstructionType type;
	protected short length;
	
    public void readFrom(ChannelBuffer data) {
        this.type = OFInstructionType.valueOf(data.readShort());
        this.length = data.readShort();
    }

    public void writeTo(ChannelBuffer data) {
        data.writeShort(type.getTypeValue());
        data.writeShort(length);
    }
	
	/**
	 * @return the type
	 */
	public OFInstructionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(OFInstructionType type) {
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
	
    /**
     * Get the length of this message, unsigned
     *
     * @return
     */
    public int getLengthU() {
        return U16.f(length);
    }
	
    /**
     * Returns a summary of the message
     * @return "ofmsg=v=$version;t=$type:l=$len:xid=$xid"
     */
    public String toString() {
        return "ofinstrution" +
            ";t=" + this.getType() +
            ";l=" + this.getLength();
    }
    
    /**
     * Given the output from toString(), 
     * create a new OFInstruction
     * @param val
     * @return
     */
    public static OFInstruction fromString(String val) {
        String tokens[] = val.split(";");
        if (!tokens[0].equals("ofinstruction"))
            throw new IllegalArgumentException("expected 'ofinstruction' but got '" + 
                    tokens[0] + "'");
        String type_tokens[] = tokens[1].split("="); 
        String len_tokens[] = tokens[2].split("=");
        OFInstruction instruction = new OFInstruction();
        
        instruction.setLength(Short.valueOf(len_tokens[1]));
        instruction.setType(OFInstructionType.valueOf(type_tokens[1]));
        return instruction;
    }
    
	/**
     * Implement clonable interface
     */
    @Override
	public OFInstruction clone() throws CloneNotSupportedException{
    	return (OFInstruction) super.clone();
	}
    
    /**
     * Set the length of this message, unsigned
     *
     * @param length
     */
    public OFInstruction setLengthU(int length) {
        this.length = U16.t(length);
        return this;
    }

	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
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
		if (!(obj instanceof OFInstruction))
			return false;
		OFInstruction other = (OFInstruction) obj;
		if (length != other.length)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
