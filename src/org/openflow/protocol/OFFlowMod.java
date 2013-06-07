/**
*    Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior
*    University
* 
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package org.openflow.protocol;

import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.factory.OFActionFactory;
import org.openflow.protocol.factory.OFActionFactoryAware;
import org.openflow.protocol.factory.OFInstructionFactory;
import org.openflow.protocol.instruction.OFInstruction;
import org.openflow.protocol.instruction.OFInstructionApplyActions;
import org.openflow.protocol.match.OFMatch2;
import org.openflow.util.U16;

/**
 * Represents an ofp_flow_mod message
 * @author David Erickson (daviderickson@cs.stanford.edu)
 *
 */
public class OFFlowMod extends OFMessage implements OFActionFactoryAware, Cloneable {
    public static int MINIMUM_LENGTH = 56;
    
    public static final byte OFPFC_ADD = 0;                /* New flow. */
    public static final byte OFPFC_MODIFY = 1;             /* Modify all matching flows. */
    public static final byte OFPFC_MODIFY_STRICT = 2;      /* Modify entry strictly matching wildcards */
    public static final byte OFPFC_DELETE=3;               /* Delete all matching flows. */
    public static final byte OFPFC_DELETE_STRICT =4;       /* Strictly match wildcards and priority. */

    // Open Flow Flow Mod Flags. Use "or" operation to set multiple flags
    public static final short OFPFF_SEND_FLOW_REM = 0x1; // 1 << 0
    public static final short OFPFF_CHECK_OVERLAP = 0x2; // 1 << 1
    public static final short OFPFF_EMERG         = 0x4; // 1 << 2
    
    protected int ofpVersion = 0;

    /*TODO DO FLOW_MOD - ACTION*/
    protected OFActionFactory actionFactory;
    protected OFInstructionFactory instructionFactory;
    protected long cookie = 0;
    protected long cookieMask = 0;
    protected byte tableId;
	protected byte command;
    protected short idleTimeout;
    protected short hardTimeout;
    protected short priority;
    protected int bufferId;
    protected int outPort;
    protected int outGroup;
	protected short flags;
	protected byte pad[] = {0x0,0x0};
    protected OFMatch match = null;
    protected OFMatch2 match2 = null;
    
    protected short matchPaddingLen = 0;
    
    protected List<OFInstruction> instructions = new LinkedList<OFInstruction> (); 
    // TODO Remove actions from anywhere
	//protected List<OFAction> actions;
    
    

	public OFFlowMod() {
        super();
        this.type = OFType.FLOW_MOD;
        this.length = U16.t(MINIMUM_LENGTH);
    }

	/**
	 * @return the tableId
	 */
	public byte getTableId() {
		return tableId;
	}

	/**
	 * @param tableId the tableId to set
	 */
	public void setTableId(byte tableId) {
		this.tableId = tableId;
	}
    
    /**
	 * @return the cookieMask
	 */
	public long getCookieMask() {
		return cookieMask;
	}

	/**
	 * @param cookieMask the cookieMask to set
	 */
	public void setCookieMask(long cookieMask) {
		this.cookieMask = cookieMask;
	}
    
    /**
     * Get buffer_id
     * @return
     */
    public int getBufferId() {
        return this.bufferId;
    }

    /**
     * Set buffer_id
     * @param bufferId
     */
    public OFFlowMod setBufferId(int bufferId) {
        this.bufferId = bufferId;
        return this;
    }

    /**
     * Get cookie
     * @return
     */
    public long getCookie() {
        return this.cookie;
    }

    /**
     * Set cookie
     * @param cookie
     */
    public OFFlowMod setCookie(long cookie) {
        this.cookie = cookie;
        return this;
    }

    /**
     * Get command
     * @return
     */
    public byte getCommand() {
        return this.command;
    }

    /**
     * Set command
     * @param command
     */
    public OFFlowMod setCommand(byte command) {
        this.command = command;
        return this;
    }


    /**
	 * @return the outGroup
	 */
	public int getOutGroup() {
		return outGroup;
	}

	/**
	 * @param outGroup the outGroup to set
	 */
	public void setOutGroup(int outGroup) {
		this.outGroup = outGroup;
	}
    
    /**
     * Get flags
     * @return
     */
    public short getFlags() {
        return this.flags;
    }

    /**
     * Set flags
     * @param flags
     */
    public OFFlowMod setFlags(short flags) {
        this.flags = flags;
        return this;
    }

    /**
     * Get hard_timeout
     * @return
     */
    public short getHardTimeout() {
        return this.hardTimeout;
    }

    /**
     * Set hard_timeout
     * @param hardTimeout
     */
    public OFFlowMod setHardTimeout(short hardTimeout) {
        this.hardTimeout = hardTimeout;
        return this;
    }

    /**
     * Get idle_timeout
     * @return
     */
    public short getIdleTimeout() {
        return this.idleTimeout;
    }

    /**
     * Set idle_timeout
     * @param idleTimeout
     */
    public OFFlowMod setIdleTimeout(short idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    /**
     * Gets a copy of the OFMatch object for this FlowMod, changes to this
     * object do not modify the FlowMod
     * @return
     */
    public OFMatch getMatch() {
        return this.match;
    }

    /**
     * Set match
     * @param match
     */
    public OFFlowMod setMatch(OFMatch match) {
        this.match = match;
        return this;
    }

    /**
     * Get out_port
     * @return
     */
    public int getOutPort() {
        return this.outPort;
    }

    /**
     * Set out_port
     * @param outPort
     */
    public OFFlowMod setOutPort(int outPort) {
        this.outPort = outPort;
        return this;
    }

    /**
     * Set out_port
     * @param port
     */
    public OFFlowMod setOutPort(OFPort port) {
        this.outPort = port.getValue();
        return this;
    }

    /**
     * Get priority
     * @return
     */
    public short getPriority() {
        return this.priority;
    }

    /**
     * Set priority
     * @param priority
     */
    public OFFlowMod setPriority(short priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Returns read-only copies of the actions contained in this Flow Mod
     * @return a list of ordered OFAction objects
     */
    public List<OFAction> getActions() {
    	return null;
        //return this.actions;
    }

    /**
     * Sets the list of actions this Flow Mod contains
     * @param actions a list of ordered OFAction objects
     */
    public OFFlowMod setActions(List<OFAction> actions) {
//        this.actions = actions;
        return this;
    }
    
    /**
	 * @return the instructions
	 */
	public List<OFInstruction> getInstructions() {
		return instructions;
	}

	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(List<OFInstruction> instructions) {
		this.instructions = instructions;
	}

    /**
	 * @return the ofpVersion
	 */
	public int getOfpVersion() {
		return ofpVersion;
	}

	/**
	 * @param ofpVersion the ofpVersion to set
	 * @return 
	 */
	public OFFlowMod setOfpVersion(int ofpVersion) {
		this.ofpVersion = ofpVersion;
		
		return this;
	}

	/**
	 * @return the match2
	 */
	public OFMatch2 getMatch2() {
		return match2;
	}

	/**
	 * @param match2 the match2 to set
	 * @return 
	 */
	public OFFlowMod setMatch2(OFMatch2 m2) {
		this.match2 = m2;
		this.length += m2.getLength() + m2.closeMatch2() - 8; // TODO DO Minimal match is included to FlowMod structure
		return this;
	}

	@Override
    public void readFrom(ChannelBuffer data) {
        super.readFrom(data);
        this.cookie = data.readLong();
        this.cookieMask = data.readLong();
        this.tableId = data.readByte();        
        this.command = data.readByte();
        this.idleTimeout = data.readShort();
        this.hardTimeout = data.readShort();
        this.priority = data.readShort();
        this.bufferId = data.readInt();
        this.outPort = data.readInt();
        this.outGroup = data.readInt();
        this.flags = data.readShort();
        this.pad[0] = data.readByte();
        this.pad[1] = data.readByte();
        if (this.match == null)
            this.match = new OFMatch();
        this.match.readFrom(data);
        
        if (this.match2 == null)
            this.match2 = new OFMatch2();
        this.match2.readFrom(data);
        
        if (this.instructionFactory == null)
        	throw new RuntimeException("OFInstructionFactory not set");
        this.instructions = this.instructionFactory.parseInstructions(data, getLengthU() 
        		- MINIMUM_LENGTH);
        		
        // TODO DO FLOW_MOD - ACTION
        if (this.actionFactory == null)
            throw new RuntimeException("OFActionFactory not set");
/*        this.actions = this.actionFactory.parseActions(data, getLengthU() -
                MINIMUM_LENGTH); */
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        super.writeTo(data);
        data.writeLong(cookie);
        data.writeLong(cookieMask);
        data.writeByte(tableId);        
        data.writeByte(command);
        data.writeShort(idleTimeout);
        data.writeShort(hardTimeout);
        data.writeShort(priority);
        data.writeInt(bufferId);
        data.writeInt(outPort);
        data.writeInt(outGroup);
        data.writeShort(flags);
        data.writeBytes(pad);
        if ((match != null) && (version == 1))
        	this.match.writeTo(data);
       
        if ((match2 != null) && (this.version > 2))
        	this.match2.writeTo(data);
        
        // TODO Remove actions
/*        if (actions != null) {
            for (OFAction action : actions) {
                action.writeTo(data);
            }
        } */
        
        if ( (instructions != null) && (! instructions.isEmpty())) {
        	for (OFInstruction instruction : instructions) {
        		instruction.writeTo(data);
        	}
        }
    }

    //TODO DO FLOW_MOD - ACTION
    @Override
    public void setActionFactory(OFActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public int hashCode() {
        final int prime = 227;
        int result = super.hashCode();
        // TODO Remove actions
//        result = prime * result + ((actions == null) ? 0 : actions.hashCode()); 
        result = prime * result + ((instructions == null) ? 0 : instructions.hashCode());
        result = prime * result + bufferId;
        result = prime * result + command;
        result = prime * result + (int) (cookie ^ (cookie >>> 32));
        result = prime * result + (int) (cookieMask ^ (cookieMask >>> 32));
        result = prime * result + tableId;        
        result = prime * result + flags;
        result = prime * result + hardTimeout;
        result = prime * result + idleTimeout;
        result = prime * result + ((match == null) ? 0 : match.hashCode());
        result = prime * result + outPort;
        result = prime * result + outGroup;
        result = prime * result + priority;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OFFlowMod)) {
            return false;
        }
        OFFlowMod other = (OFFlowMod) obj;
        
        // TODO DO - FLOW_MOD - ACTION
        /*if (actions == null) {
            if (other.actions != null) {
                return false;
            }
        } else if (!actions.equals(other.actions)) {
            return false;
        }*/
        
        if (instructions == null) {
            if (other.instructions != null) {
                return false;
            }
        } else if (!instructions.equals(other.instructions)) {
            return false;
        }
        
        if (bufferId != other.bufferId) {
            return false;
        }
        if (command != other.command) {
            return false;
        }
        if (cookie != other.cookie) {
            return false;
        }
        if (cookieMask != other.cookieMask ) {
        	return false;
        }
        if (tableId != other.tableId ) {
        	return false;
        }
        if (flags != other.flags) {
            return false;
        }
        if (hardTimeout != other.hardTimeout) {
            return false;
        }
        if (idleTimeout != other.idleTimeout) {
            return false;
        }
        if (match == null) {
            if (other.match != null) {
                return false;
            }
        } else if (!match.equals(other.match)) {
            return false;
        }
        if (outPort != other.outPort) {
            return false;
        }
        if (outGroup != other.outGroup) {
            return false;
        }
        if (priority != other.priority) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public OFFlowMod clone() throws CloneNotSupportedException {
        OFMatch neoMatch = match.clone();
        OFMatch2 neoMatch2 = match2.clone();
        OFFlowMod flowMod= (OFFlowMod) super.clone();
        flowMod.setMatch(neoMatch);
        flowMod.setMatch2(neoMatch2);
        List<OFAction> neoActions = new LinkedList<OFAction>();
//TODO DO FLOW_MOD - ACTION
        /*        for(OFAction action: this.actions)
            neoActions.add((OFAction) action.clone()); */
        flowMod.setActions(neoActions);
        
        List<OFInstruction> neoInstructions = new LinkedList<OFInstruction>();
        for(OFInstruction instruction: this.instructions)
            neoInstructions.add((OFInstruction) instruction.clone());
        flowMod.setInstructions(neoInstructions);
        
        return flowMod;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OFFlowMod [actionFactory=" + instructionFactory + ", bufferId=" + bufferId 
        		+ ", command=" + command + ", cookie=" + cookie 
        		+ ", cookieMask=" + cookieMask + ", tableId=" + tableId 
        		+ ", flags=" + flags + ", hardTimeout=" + hardTimeout 
        		+ ", idleTimeout=" + idleTimeout + ", match=" + match 
        		+ ", outPort=" + outPort + ", outGroup=" + outGroup
                + ", priority=" + priority + ", length=" + length 
                + ", type=" + type + ", version=" + version + ", xid=" + xid + "]";
    }
// OF 1.2
	public void addInstruction(OFInstructionApplyActions instruction) {
		instructions.add(instruction);
		
		this.length += instruction.getLengthU();	
	}
}
