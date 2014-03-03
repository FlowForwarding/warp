package org.flowforwarding.warp.protocol.ofitems;

import org.apache.avro.generic.GenericContainer;

public class OFField implements IOFItem{
   
   protected String name;
   protected String parent;
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
   public String getParent() {
      return parent;
   }
   public void setParent(String parent) {
      this.parent = parent;
   }


   @Override
   public GenericContainer get() {
      
      return null;
   }
}
