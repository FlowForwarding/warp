package org.flowforwarding.warp.protocol.offields;

import org.apache.avro.generic.GenericContainer;

public class OFField implements IOFItem{
   
   protected String name;
   protected String value;
   protected byte size;

   
   public byte getSize() {
      return size;
   }
   public void setSize(byte size) {
      this.size = size;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getValue() {
      return value;
   }
   public void setValue(String value) {
      this.value = value;
   }

   @Override
   public GenericContainer get() {
      
      return null;
   }
}
