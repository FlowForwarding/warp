package org.flowforwarding.warp.protocol.supply;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Fixed;

public class MethodGetFixedFromArray implements IMethodGet<byte[], Fixed>{

   protected Schema schema;
   
   public MethodGetFixedFromArray(Schema sch) {
      schema = sch;
   }
   
   @Override
   public Fixed get(byte[] in) {

      return new GenericData.Fixed(schema, in);
   }

}
