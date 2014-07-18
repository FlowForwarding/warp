/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container;

/**
 * @author Infoblox Inc.
 *
 */
public interface INamedValue <External, Internal> {
   public String name(); 
   public Internal get();
}
