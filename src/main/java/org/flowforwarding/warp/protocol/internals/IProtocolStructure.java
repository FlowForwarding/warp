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
   public void add(IProtocolItem<External, Internal> value);
   public byte[] encode();
}
