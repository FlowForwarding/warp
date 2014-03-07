package org.flowforwarding.warp.protocol.ofitems;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;

public class OFItemRecordBuilder implements IOFItemBuilder{
   
   protected String name;
   protected Schema schema;
   protected Map<String, IOFItemBuilder> itemBuilders = new HashMap<>();

   public OFItemRecordBuilder (String nm, Schema sch) {
      name = nm;
      schema = sch;
   }
   
   @Override
   public IOFItem build() {
      // TODO Improvs: Should it be a static??
      // TODO Improvs: Close OFField constructor, build via reference??
      OFItemRecord struct = new OFItemRecord(name, schema);
      
      for (String nm: itemBuilders.keySet()) {
         struct.addItem(itemBuilders.get(nm).build());
      }
      return struct;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   public void addItemBuilder (String nm, IOFItemBuilder builder) {
      itemBuilders.put(nm, builder);
   }

   /**
    * @return the schema
    */
   public Schema getSchema() {
      return schema;
   }
}
