package org.flowforwarding.warp.protocol.ofitems;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.*;
import org.apache.avro.generic.GenericData.Fixed;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

public class OFItemEnumBuilder implements IOFItemBuilder{

   protected String name;
   protected Schema schema;
   protected Schema itemsType;
   
   protected Map <Fixed, String> keys = null;  
   protected Map <String, Fixed> values = null;

   @Override
   public IOFItem build() {
   // TODO Improvs: Should it be a static??
   // TODO Improvs: Close OFItemEnum constructor, build via reference or any kind of 'friend' class??
      OFItemEnum enm = new OFItemEnum(name, schema, null);

      return enm;
   }
   
// TODO Improvs: Should it be declared in the IOFItemBuilder?
   public IOFItem build(Fixed in) {
   // TODO Improvs: Should it be a static??
   // TODO Improvs: Close OFItemEnum constructor, build via reference or any kind of 'friend' class??
      String itemName = keys.get(in);
      
      OFItemEnum enm = new OFItemEnum(itemName, this.itemsType, in);

      return enm;
   }
   
   public OFItemEnumBuilder(String nm, Schema sch) {
      name = nm;
      schema = sch;
      
      itemsType = schema.getEnumItemsSchema();
      
      keys = new HashMap <>();
      values = new HashMap <>();
      
      List<String> symbols = schema.getEnumSymbols();
      for (String n : symbols) {
         // TODO Improv: currently Enum returns ONLY JsonNode. We must implement that it returns instance corresponding with enumItemsSchema! 
         JsonNode item = schema.getEnumItem(n);
         
         if (item.isArray()) {
            int size = item.size();
            byte[] val = new byte[size];
            
            for (int i=0; i<size; i++) {
               val[i] = (byte)item.get(0).asInt();
            }
            
            Fixed rec = new Fixed(itemsType, val);
            
            keys.put(rec, n);
            values.put(n, rec);
         }         
      }

   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }
}
