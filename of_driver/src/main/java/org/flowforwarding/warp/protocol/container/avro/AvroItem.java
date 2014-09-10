/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container.avro;

import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.container.IAtom;
import org.flowforwarding.warp.protocol.container.IBinary;
import org.flowforwarding.warp.protocol.container.IBuilt;
import org.flowforwarding.warp.protocol.container.INamedValue;
import org.flowforwarding.warp.protocol.container.IStructure;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroItem implements IBuilt<String, GenericContainer>,
                                 IAtom<String, GenericContainer>,
                                 IStructure<String, GenericContainer>,
                                 INamedValue<String, GenericContainer>,
                                 IBinary<String, GenericContainer>{

   private IBuilt<String, GenericContainer> item;
   
   public AvroItem (IBuilt<String, GenericContainer> i) {
      item = i;
   }

   @Override
   public String name() {
      if (item instanceof INamedValue) {
         return ((INamedValue) item).name();
      } else return null;
      // TODO Improvs: exception
   }

   @Override
   public GenericContainer get() {
      return item.get();
   }

   @Override
   public void set(String name, GenericContainer value) {
      //TODO I: error handling must be here
      ((IStructure)item).set(name, value);
   }

   @Override
   public void set(String name, byte[] value) {
      //TODO I: error handling must be here      
      ((IStructure)item).set(name, value);
   }

   @Override
   public void set(byte[] value) {
      ((IAtom)item).set(value);
   }

   @Override
   public void set(GenericContainer value) {
   }

   @Override
   public byte[] binary() {
      return ((IBinary)item).binary();
   }

   @Override
   public byte[] binary(String name) {
      return ((IBinary)item).binary(name);
   }

   @Override
   public INamedValue<String, GenericContainer> field(String name) {
      return ((IStructure)item).field(name);
   }
}
