/**
 * 
 */
package org.openflow.protocol.instruction;


/**
 * @author Dmitry Orekhov (dmitry.orekhov@gmail.com)
 */
public class OFInstructionGoto extends OFInstruction implements Cloneable {
	
	public static int MINIMUM_LENGTH = 8;
	
	protected byte tableId = 0;
	protected byte[] pad = {0x0, 0x0, 0x0};
}