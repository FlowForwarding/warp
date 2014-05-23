/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals;

/**
 * @author Infoblox Inc.
 *
 */
public interface IProtocolStructure <External, Internal> extends IProtocolItem <External, Internal>{
   public Internal get();
   public IProtocolItem<External, Internal> get(String name);
   public void add(IProtocolItem<External, Internal> value);
   public void set(String name, byte[] value);
   public byte[] binary(String name);
   public byte[] binary();
}
