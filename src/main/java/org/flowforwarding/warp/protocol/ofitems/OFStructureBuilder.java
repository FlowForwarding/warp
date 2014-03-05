package org.flowforwarding.warp.protocol.ofitems;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;

public class OFStructureBuilder implements IOFItemBuilder{
   
   protected String name;
   protected Schema schema;
   protected Map<String, IOFItemBuilder> itemBuilders = new HashMap<>();

   public OFStructureBuilder (String nm, Schema sch) {
      System.out.println("BUILDER, STRUCTURE -- Name: " + name + ", Schema: " + sch.getName() + ", Type: " + sch.getType());
      
      name = nm;
      schema = sch;
   }
   
   @Override
   public IOFItem build() {
      // TODO Improvs: Should it be a static??
      // TODO Improvs: Close OFField constructor, build via reference??
      OFStructure struct = new OFStructure(name, schema);
      
      for (String name: itemBuilders.keySet()) {
         struct.addItem(itemBuilders.get(name).build());
      }
      return struct;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   public void addItemBuilder (String name, IOFItemBuilder builder) {
      itemBuilders.put(name, builder);
   }

   /**
    * @return the schema
    */
   public Schema getSchema() {
      return schema;
   }
}
