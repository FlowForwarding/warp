/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolItem;

/**
 * @author Infoblox Inc.
 *
 */
public abstract class AvroItem implements IProtocolItem <String, GenericContainer>{

   @Override
   public String name() { return null; }

   @Override
   public GenericContainer get() { return null; }

   public void add(AvroItem avroItem) { // TODO Auto-generated method stub
   }
   public void add(String name, AvroItem avroItem) {// TODO Auto-generated method stub
   }
   public void set(String name, byte[] array) {
      // TODO Auto-generated method stub
   }

/*   public GenericContainer get(String name) { return null; }
   public void add(IProtocolItem<String, GenericContainer> item) {}
   public byte[] binary() { return null; }
   public void set(String name, byte[] value) { }
   public byte[] binary(String name) { return null; }
   public void add(AvroItem item) {}
   public void add(String name, AvroItem item) {}*/

}
