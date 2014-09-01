/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container;

/**
 * @author Infoblox Inc.
 *
 */
public interface IContainer <External, Internal> {
   public void init();
   public IStructure <External, Internal> structure(String structureName, byte []... in);
   public IAtom <External, Internal> atom(String atomName, byte []... in);
}
