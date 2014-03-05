package org.flowforwarding.warp.protocol.ofitems;

import org.apache.avro.Schema;

public class OFFieldBuilder implements IOFItemBuilder{

   protected String name;
   protected Schema schema;
   
   
   @Override
   public IOFItem build() {
   // TODO Improvs: Should it be a static??
   // TODO Improvs: Close OFField constructor, build via reference??
      OFField field = new OFField(name, schema);
      
      return field;
   }
   
   public OFFieldBuilder(String nm, Schema sch) {
      name = nm;
      schema = sch;
      
      System.out.println("     BUILDER, FIELD -- Name: " + name + ", Schema: " + sch.getName() + ", Type: " + sch.getType() + ", Size: " + sch.getFixedSize());
      
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }
}
