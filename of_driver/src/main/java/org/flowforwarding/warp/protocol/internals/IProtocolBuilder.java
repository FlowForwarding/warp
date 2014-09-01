/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals;

/**
 * @author Infoblox Inc.
 *
 */
public interface IProtocolBuilder <External, Internal>{
   public IProtocolItem <External, Internal> build();
}
