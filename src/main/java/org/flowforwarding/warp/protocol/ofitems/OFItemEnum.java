package org.flowforwarding.warp.protocol.ofitems;

import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Fixed;
import org.codehaus.jackson.JsonNode;

public class OFItemEnum implements IOFItem{
   protected String name;
   protected Schema schema;
   
   // TODO Improv: Fixed? array? We should think about it.
   protected Fixed value;
   
   @Override
   public GenericContainer get() {
      // TODO Improve: catch possible exception. It will be thrown in case inconsistence between value and schema
      return null;

   }
   
   public OFItemEnum (String nm, Schema sch, Fixed v) {
      name = nm;
      schema = sch;
      value = v;
   }
   
   public String getName() {
      return name;
   }

   @Override
   public Schema getSchema() {
      return schema;
   }

   /**
    * @return the value
    */
   public Fixed getValue() {
      return value;
   }
}
