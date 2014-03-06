package org.flowforwarding.warp.protocol.ofitems;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;

public interface IOFItem {
   
   public GenericContainer get();
   
   public String getName();
   public Schema getSchema();
}
