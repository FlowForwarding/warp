package org.openflow.protocol.match;
import org.jboss.netty.buffer.ChannelBuffer;

public class OFMatchOXM implements Cloneable {
	
	protected int oxmHeader = 0;
	protected short length = 0;
	
	/**
	 * @param oxmHeader
	 */
	public OFMatchOXM(int oxmHeader) {
		super();
		this.oxmHeader = oxmHeader;
	}
	
	public short getLength() {
		return length;
	}

	public void writeTo(ChannelBuffer data) { 
		data.writeInt(oxmHeader);
	}

	/**
	 * @return the oxmHeader
	 */
	public int getOxmHeader() {
		return oxmHeader;
	}

	/**
	 * @param oxmHeader the oxmHeader to set
	 */
	public void setOxmHeader(int oxmHeader) {
		this.oxmHeader = oxmHeader;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + oxmHeader;
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
		if (!(obj instanceof OFMatchOXM))
			return false;
		OFMatchOXM other = (OFMatchOXM) obj;
		if (oxmHeader != other.oxmHeader)
			return false;
		return true;
	}
	
}
