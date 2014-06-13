package org.flowforwarding.warp.protocol.internals.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolItem;

public class AvroUnionField implements IProtocolItem<String, GenericContainer> {
   
   protected String name;
   protected Schema schema;
   protected GenericContainer value;
   
   private AvroUnionField (AvroUnionBuilder builder) {
      name = builder.name;
      schema = builder.schema;
   }
   
   @Override
   public GenericContainer get() { return value; }
   @Override
   public String name() { return name; }
   
   public static class AvroUnionBuilder extends AvroItemBuilder {
      protected final String name;
      protected final Schema schema;
      protected GenericContainer value = null;
      
      @Override
      public IProtocolItem<String, GenericContainer> build() {
         return new AvroUnionField(this);
      }
      
      public AvroUnionBuilder(String nm, Schema sch) {
         name = nm;
         schema = sch;
      }
      
      @Override
      public AvroUnionBuilder value (byte [] in) {
         return this;
      }
      @Override
      public AvroUnionBuilder value(GenericContainer in) {
         this.value = in;
         return this;
      }
   } 
}
