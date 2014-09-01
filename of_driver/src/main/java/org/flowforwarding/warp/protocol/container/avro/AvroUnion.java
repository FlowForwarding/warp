/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.container.IBuilder;
import org.flowforwarding.warp.protocol.container.IBuilt;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroUnion {

/*   protected String name;
   protected Schema schema;
   protected GenericContainer value;
   
   private AvroUnion (AvroUnionBuilder builder) {
      name = builder.name;
      schema = builder.schema;
   }
   
   @Override
   public GenericContainer get() { return value; }
   @Override
   public String name() { return name; }
   
   public static class AvroUnionBuilder implements IBuilder<String, GenericContainer> {
      protected final String name;
      protected final Schema schema;
      protected GenericContainer value = null;
      
      @Override
      public AvroUnion build() {
         return new AvroUnion(this);
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
   }*/
}
