/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container.avro;

import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.container.IAtom;
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
                                 INamedValue<String, GenericContainer>{

   private IBuilt<String, GenericContainer> item;

   @Override
   public String name() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public GenericContainer get() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void set(String name, GenericContainer value) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void set(String name, byte[] value) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void set(byte[] value) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void set(GenericContainer value) {
      // TODO Auto-generated method stub
      
   }

}
