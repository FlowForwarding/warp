package org.flowforwarding.warp.protocol.ofitems;

import org.apache.avro.generic.GenericContainer;

public class OFField implements IOFItem{
   
   protected String name;
   protected String parent;

   protected int size;
   protected byte[] value;
   
   @Override
   public GenericContainer get() {
      
      return null;
   }
   
   public OFField (String nm, int sz) {
      name = nm;
      size = sz;
      
      value = new byte[size];
   }
   
   public int getSize() {
      return size;
   }
   public void setSize(int size) {
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
}
