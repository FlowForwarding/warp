/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container;

/**
 * @author Infoblox Inc.
 *
 */
public interface IBuilder <External, Internal> {
   public IBuilt <External, Internal> build();
   
   public IBuilder <External, Internal> value(byte[] in);
   public IBuilder <External, Internal> value(Internal in);

}
