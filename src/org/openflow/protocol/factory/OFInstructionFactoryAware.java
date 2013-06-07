package org.openflow.protocol.factory;

public interface OFInstructionFactoryAware {
	
    /**
     * Sets the OFActionFactory
     * @param actionFactory
     */
    public void setInstructionFactory(OFInstructionFactory instructionFactory);

}
