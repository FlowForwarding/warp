package org.flowforwarding.warp.protocol.ofitems;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;

public class OFItemFixed implements IOFItem{
   
   protected String name;

   protected int size;
   protected byte[] value = null;
   protected Schema schema;
   
   @Override
   public GenericContainer get() {
      // TODO Improve: catch possible exception. It will be thrown in case inconsistence between value and schema
      if (value != null)
         return new GenericData.Fixed(schema, value);
      else return null;
   }
   
   public OFItemFixed (String nm, Schema sch) {
      name = nm;
      schema = sch;
  // TODO Improv: catch possible exception. It will be thrown in case Schema is NOT Fixed
      size = sch.getFixedSize();
   }
   
   public int getSize() {
      return size;
   }

   public String getName() {
      return name;
   }

   /**
    * @return the value
    */
   public byte[] getValue() {
      return value;
   }
   /**
    * @param value the value to set
    */
   public void setValue(byte[] value) {
      this.value = value;
   }

   @Override
   public Schema getSchema() {
      return schema;
   }
}
