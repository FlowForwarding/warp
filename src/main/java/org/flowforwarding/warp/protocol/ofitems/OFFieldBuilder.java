package org.flowforwarding.warp.protocol.ofitems;

public class OFFieldBuilder implements IOFItemBuilder{

   protected String name;
   protected String parent;   
   
   @Override
   public IOFItem build() {
   // TODO Improvs: Should it be a static??
   // TODO Improvs: Close OFField constructor, build via reference??

      OFField field = new OFField();
      field.setName(name);
      
      return field;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the parent
    */
   public String getParent() {
      return parent;
   }

   /**
    * @param parent the parent to set
    */
   public void setParent(String parent) {
      this.parent = parent;
   }

}
