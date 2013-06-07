package org.openflow.protocol.instruction;

import java.lang.reflect.Constructor;

import org.openflow.protocol.Instantiable;


/**
 * List of OpenFlow Instruction types and mappings to wire protocol value and
 * derived classes
 *
 * @author David Erickson (daviderickson@cs.stanford.edu)
 * @author Dmitry Orekhov (dmitry.orekhov@gmail.com)
 */
public enum OFInstructionType {
	// TODO DO Apply aspects here??	
	OFPIT_GOTO_TABLE              (1, OFInstructionGoto.class, new Instantiable<OFInstruction>() {
									  @Override
									  public OFInstruction instantiate() {
										  return new OFInstructionGoto();
									  }}),
									
	OFPIT_WRITE_METADATA          (2, OFInstructionWriteMetadata.class, new Instantiable<OFInstruction>() {
                                       @Override
                                       public OFInstruction instantiate() {
                                    	   return new OFInstructionGoto();
                                       }}),									
    OFPIT_WRITE_ACTIONS           (3, OFInstructionWriteActions.class, new Instantiable<OFInstruction>() {
  									  @Override
  									  public OFInstruction instantiate() {
  										  return new OFInstructionGoto();
  									  }}),
  									
    OFPIT_APPLY_ACTIONS           (4, OFInstructionApplyActions.class, new Instantiable<OFInstruction>() {
                                      @Override
                                      public OFInstruction instantiate() {
                                    	  return new OFInstructionGoto();
                                      }}),                                       
    OFPIT_CLEAR_ACTIONS           (5, OFInstructionClearActions.class, new Instantiable<OFInstruction>() {
                                      @Override
                                      public OFInstruction instantiate() {
                                    	  return new OFInstructionGoto();
                                      }});									

    protected static OFInstructionType[] mapping;

    protected Class<? extends OFInstruction> clazz;
    protected Constructor<? extends OFInstruction> constructor;
    protected Instantiable<OFInstruction> instantiable;
    protected int minLen;
    protected short type;
    
    /**
     * Store some information about the OpenFlow Instruction type, including wire
     * protocol type number, length, and derrived class
     *
     * @param type Wire protocol number associated with this OFType
     * @param clazz The Java class corresponding to this type of OpenFlow Instruction
     * @param instantiable the instantiable for the OFInstruction this type represents
     */
    OFInstructionType (int type, Class<? extends OFInstruction> clazz, Instantiable<OFInstruction> instantiable) {
        this.type = (short) type;
        this.clazz = clazz;
        this.instantiable = instantiable;
        try {
            this.constructor = clazz.getConstructor(new Class[]{});
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failure getting constructor for class: " + clazz, e);
        }
        OFInstructionType.addMapping(this.type, this);
    }    
    
    /**
     * Adds a mapping from type value to OFInstructionType enum
     *
     * @param i OpenFlow wire protocol Instruction type value
     * @param t type
     */
    static public void addMapping(short i, OFInstructionType t) {
        if (mapping == null)
            mapping = new OFInstructionType[16];
        // bring higher mappings down to the edge of our array
        if (i < 0)
            i = (short) (16 + i);
        OFInstructionType.mapping[i] = t;
    }
    
    /**
     * Given a wire protocol OpenFlow type number, return the OFType associated
     * with it
     *
     * @param i wire protocol number
     * @return OFType enum type
     */

    static public OFInstructionType valueOf(short i) {
        if (i < 0)
            i = (short) (16+i);
        return OFInstructionType.mapping[i];
    }

    /**
     * @return Returns the wire protocol value corresponding to this
     *         OFInstructionType
     */
    public short getTypeValue() {
        return this.type;
    }

    /**
     * @return return the OFInstruction subclass corresponding to this OFInstructionType
     */
    public Class<? extends OFInstruction> toClass() {
        return clazz;
    }

    /**
     * Returns the no-argument Constructor of the implementation class for
     * this OFInstructionType
     * @return the constructor
     */
    public Constructor<? extends OFInstruction> getConstructor() {
        return constructor;
    }

    /**
     * Returns a new instance of the OFInstruction represented by this OFInstructionType
     * @return the new object
     */
    public OFInstruction newInstance() {
        return instantiable.instantiate();
    }

    /**
     * @return the instantiable
     */
    public Instantiable<OFInstruction> getInstantiable() {
        return instantiable;
    }

    /**
     * @param instantiable the instantiable to set
     */
    public void setInstantiable(Instantiable<OFInstruction> instantiable) {
        this.instantiable = instantiable;
    }   
    
}
