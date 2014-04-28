/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals;

/**
 * @author Infoblox Inc.
 *
 */
public interface IProtocolContainer <External, Internal>{
   public void init();
   public void init(String src);
   
   public IProtocolStructure <External, Internal> getStructure(String structureName);
}
