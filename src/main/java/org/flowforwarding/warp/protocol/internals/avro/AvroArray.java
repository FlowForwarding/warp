package org.flowforwarding.warp.protocol.internals.avro;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.flowforwarding.warp.protocol.internals.IProtocolItem;

//public class AvroArray implements IProtocolItem <String, GenericContainer>{
public class AvroArray extends AvroItem{
// TODO Improvs: Should we replace IProtocolItem with AvroItem or something?
   
   protected String name;
   protected Schema schema;
   protected Schema itemSchema;
   
   protected List<GenericContainer> array = new ArrayList<>();
   
   protected GenericContainer value;
   
   private AvroArray (AvroArrayBuilder builder) {
      name = builder.name;
      schema = builder.schema;
   }
   
   @Override
   public String name() { return name; }

   @Override
   public GenericContainer get() {
      return new GenericData.Array<>(Schema.createArray(schema), array); 
   }
   
   public void add (GenericContainer item) {
      array.add(item);
   }
   
   public void add (IProtocolItem <String, GenericContainer> item) {
      array.add(item.get());
   }
   
   public static class AvroArrayBuilder extends AvroItemBuilder {
      protected final String name;
      protected final Schema schema;
      protected GenericContainer value = null;
      
      @Override
      public IProtocolItem<String, GenericContainer> build() {
         return new AvroArray(this);
      }
      
      public AvroArrayBuilder(String nm, Schema sch) {
         name = nm;
         schema = sch;
      }
      
      @Override
      public AvroArrayBuilder value (byte [] in) {
         return this;
      }
      @Override
      public AvroArrayBuilder value(GenericContainer in) {
         this.value = in;
         return this;
      }
   } 
}
