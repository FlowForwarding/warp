/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolBuilder;
import org.flowforwarding.warp.protocol.internals.avro.AvroRecord.AvroRecordBuilder;

/**
 * @author Infoblox Inc.
 *
 */
public abstract class AvroItemBuilder implements IProtocolBuilder<String, GenericContainer>{

   // TODO Improvs: Should it to be created Value methods in IProtocolBuilder?  
   /**
    * @param in
    * @return
    */
   public abstract AvroItemBuilder Value(byte[] in);
   /**
    * @param in
    * @return
    */
   public abstract AvroItemBuilder Value(GenericContainer in);

}
