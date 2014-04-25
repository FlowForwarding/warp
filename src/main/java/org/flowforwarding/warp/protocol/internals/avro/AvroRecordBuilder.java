/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolBuilder;
import org.flowforwarding.warp.protocol.internals.IProtocolItem;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroRecordBuilder extends AvroItemBuilder{
   protected String name;
   protected Schema schema;
   protected Map<String, IProtocolBuilder<String, GenericContainer>> builders = new HashMap<>();

   public AvroRecordBuilder (String nm, Schema sch) {
      name = nm;
      schema = sch;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.internals.IProtocolBuilder#build()
    */
   @Override
   public IProtocolItem<String, GenericContainer> build() {
      // TODO Improvs: Should it be a static??
      // TODO Improvs: Close OFField constructor, build via reference??
      AvroRecord rec = new AvroRecord(name, schema);
      
      for (String nm: builders.keySet()) {
         rec.add(builders.get(nm).build());
      }
      return rec;
   }
   
   public String getName() {
      return name;
   }
   
   public void addItemBuilder (String nm, IProtocolBuilder<String, GenericContainer> builder) {
      builders.put(nm, builder);
   }
   
   public Schema getSchema() {
      return schema;
   }
}

