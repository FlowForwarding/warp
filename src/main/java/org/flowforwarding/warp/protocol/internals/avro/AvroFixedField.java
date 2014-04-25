/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import org.flowforwarding.warp.protocol.internals.IProtocolAtom;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroFixedField implements IProtocolAtom <String, GenericContainer>{
   
   protected String name;

   protected int size;
   protected byte[] value = null;
   protected Schema schema;

   public AvroFixedField (String nm, Schema sch) {
      name = nm;
      schema = sch;
  // TODO Improv: catch possible exception. It will be thrown in case Schema is NOT Fixed
      size = sch.getFixedSize();
   }

   @Override
   public GenericContainer get() {
      // TODO Improve: catch possible exception. It will be thrown in case inconsistence between value and schema
      if (value != null)
         return new GenericData.Fixed(schema, value);
      else return null;
   }

   @Override
   public void set(String value) {
      // TODO Auto-generated method stub
      return;
   }
   
   public int getSize() {
      return size;
   }

   public String getName() {
      return name;
   }

   public byte[] getValue() {
      return value;
   }

   public Schema getSchema() {
      return schema;
   }

}
