/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData.Fixed;
import org.flowforwarding.warp.protocol.internals.IProtocolAtom;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroEnum implements IProtocolAtom <String, GenericContainer> {

   protected String name;
   protected Schema schema;
   
   // TODO Improv: Fixed? array? We should think about it.
   protected Fixed value;
   
   @Override
   public GenericContainer get() {
      // TODO Improve: catch possible exception. It will be thrown in case inconsistence between value and schema
      return null;

   }
   
   public AvroEnum (String nm, Schema sch, Fixed v) {
      name = nm;
      schema = sch;
      value = v;
   }
   
   public String getName() {
      return name;
   }

   public Schema getSchema() {
      return schema;
   }

   public Fixed getValue() {
      return value;
   }

   @Override
   public void set(String value) {
      // TODO Improve: throw Exception??!
   }

}
