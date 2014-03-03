package org.flowforwarding.warp.protocol.ofitems;

import java.util.HashMap;
import java.util.Map;

public class OFStructureBuilder implements IOFItemBuilder{
   
   protected String name;
   protected Map<String, IOFItemBuilder> itemBuilders = new HashMap<>();

   @Override
   public IOFItem build() {
      return null;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }
   
   public void addItemBuilder (String name, IOFItemBuilder builder) {
      itemBuilders.put(name, builder);
   }

}
