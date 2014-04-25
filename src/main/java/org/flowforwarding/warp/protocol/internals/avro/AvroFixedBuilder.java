/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolBuilder;
import org.flowforwarding.warp.protocol.internals.IProtocolItem;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroFixedBuilder extends AvroItemBuilder {

   protected String name;
   protected Schema schema;
   
   @Override
   public IProtocolItem<String, GenericContainer> build() {
      // TODO Improvs: Should it be a static??
      // TODO Improvs: Close OFField constructor, build via reference??
      AvroFixedField field = new AvroFixedField(name, schema);
         
      return field;
   }
   
   public AvroFixedBuilder(String nm, Schema sch) {
      name = nm;
      schema = sch;
   }

   public String getName() {
      return name;
   }
}
