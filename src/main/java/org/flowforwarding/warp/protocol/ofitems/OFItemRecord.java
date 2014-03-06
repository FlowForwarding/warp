package org.flowforwarding.warp.protocol.ofitems;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;

public class OFItemRecord implements IOFItem{
   
   protected String name;
   protected Schema schema;
   protected List<IOFItem> items = new ArrayList<>();

   public OFItemRecord (String nm, Schema sch) {
      name = nm;
      schema = sch;
   }
   
   @Override
   public GenericContainer get() {
      
      GenericRecordBuilder builder = new GenericRecordBuilder(schema);
      for (IOFItem item : items) {
         GenericContainer val = item.get();
         // TODO Improv. I dislike this NULL verification
         if (val != null)
            builder.set(item.getName(), val);
      }
      
      GenericRecord record = builder.build();
      return record;
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

   @Override
   public Schema getSchema() {
      return schema;
   }

}
