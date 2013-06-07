package org.openflow.protocol.factory;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openflow.protocol.instruction.OFInstruction;
import org.openflow.protocol.instruction.OFInstructionType;

public interface OFInstructionFactory {
    /**
     * Retrieves an OFInstruction instance corresponding to the specified
     * OFInstructionType
     * @param t the type of the OFInstruction to be retrieved
     * @return an OFInstruction instance
     */
    public OFInstruction getInstruction(OFInstructionType t);

    /**
     * Attempts to parse and return all OFInstructions contained in the given
     * ByteBuffer, beginning at the ByteBuffer's position, and ending at
     * position+length.
     * @param data the ChannelBuffer to parse for OpenFlow actions
     * @param length the number of Bytes to examine for OpenFlow actions
     * @return a list of OFInstruction instances
     */
    public List<OFInstruction> parseInstructions(ChannelBuffer data, int length);

    /**
     * Attempts to parse and return all OFInstructions contained in the given
     * ByteBuffer, beginning at the ByteBuffer's position, and ending at
     * position+length.
     * @param data the ChannelBuffer to parse for OpenFlow actions
     * @param length the number of Bytes to examine for OpenFlow actions
     * @param limit the maximum number of messages to return, 0 means no limit
     * @return a list of OFInstruction instances
     */
    public List<OFInstruction> parseInstructions(ChannelBuffer data, int length, int limit);
}
