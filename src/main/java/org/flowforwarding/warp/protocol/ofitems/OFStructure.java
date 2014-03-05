package org.flowforwarding.warp.protocol.ofitems;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;

public class OFStructure implements IOFItem{
   
   protected String name;
   protected Schema schema;
   protected List<IOFItem> items = new ArrayList<>();

   public OFStructure (String nm, Schema sch) {
      name = nm;
      schema = sch;
   }
   
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

   public void addItem (IOFItem item) {
      items.add(item);
   }

}
