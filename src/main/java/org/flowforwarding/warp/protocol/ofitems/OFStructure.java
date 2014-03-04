package org.flowforwarding.warp.protocol.ofitems;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericContainer;

public class OFStructure implements IOFItem{
   
   protected String name;
   protected List<IOFItem> items = new ArrayList<>();

   @Override
   public GenericContainer get() {

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
   
   public void addItem (IOFItem item) {
      items.add(item);
   }

}
