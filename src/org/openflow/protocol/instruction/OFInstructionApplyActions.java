package org.openflow.protocol.instruction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.action.OFAction;

public class OFInstructionApplyActions extends OFInstruction implements Cloneable {
	
	public static int MINIMUM_LENGTH = 8;

    protected byte pad[] = {0x0, 0x0, 0x0, 0x0};
    protected List<OFAction> actions;
    
    public OFInstructionApplyActions() {
        super.setType(OFInstructionType.OFPIT_APPLY_ACTIONS);
        super.setLength((short) MINIMUM_LENGTH);
        
        this.actions = new LinkedList<OFAction>();
        
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
        
        int count = 0;
        
        if (! actions.isEmpty() ) {
            for (OFAction action : actions) {
                action.writeTo(data);
            }
        }
    }
    
    public void addAction(OFAction action) {
    	actions.add(action);
    	this.length += action.getLength();    	
    }
    
    public OFInstructionApplyActions setActions (List<OFAction> actions) {
    	this.actions = actions;
    	
    	return this;
    }
    
    
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
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
		if (!(obj instanceof OFInstructionApplyActions))
			return false;
		OFInstructionApplyActions other = (OFInstructionApplyActions) obj;
		if (actions == null) {
			if (other.actions != null)
				return false;
		} else if (!actions.equals(other.actions))
			return false;
		if (!Arrays.equals(pad, other.pad))
			return false;
		return true;
	}

}
