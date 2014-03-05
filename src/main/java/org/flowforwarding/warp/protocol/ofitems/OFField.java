package org.flowforwarding.warp.protocol.ofitems;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;

public class OFField implements IOFItem{
   
   protected String name;
   protected String parent;

   protected int size;
   protected byte[] value = null;
   protected Schema schema;
   
   @Override
   public GenericContainer get() {
      
      return null;
   }
   
   public OFField (String nm, Schema sch) {
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

   public String getParent() {
      return parent;
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

   /**
    * @return the schema
    */
   public Schema getSchema() {
      return schema;
   }

   /**
    * @param schema the schema to set
    */
   public void setSchema(Schema schema) {
      this.schema = schema;
   }
}
