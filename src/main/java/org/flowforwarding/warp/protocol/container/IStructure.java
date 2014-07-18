/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container;

/**
 * @author Infoblox Inc.
 *
 */
public interface IStructure <External, Internal> {
   public void set (String name, Internal value);
   public void set (String name, byte[] value);
}
