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
   public IProtocolStructure <External, Internal> getStructure(String structureName, byte []... in);
   public IProtocolAtom <External, Internal> getAtom(String atomName, byte []... in);
}
