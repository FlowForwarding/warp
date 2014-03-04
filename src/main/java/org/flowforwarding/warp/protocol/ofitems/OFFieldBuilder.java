package org.flowforwarding.warp.protocol.ofitems;

public class OFFieldBuilder implements IOFItemBuilder{

   protected String name;
   protected int size;
   
   @Override
   public IOFItem build() {
   // TODO Improvs: Should it be a static??
   // TODO Improvs: Close OFField constructor, build via reference??
      OFField field = new OFField(name, size);
      
      return field;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the size
    */
   public int getSize() {
      return size;
   }

   /**
    * @param size the size to set
    */
   public void setSize(int size) {
      this.size = size;
   }
}
