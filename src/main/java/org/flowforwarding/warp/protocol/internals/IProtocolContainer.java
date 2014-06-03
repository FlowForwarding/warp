/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals;

import org.apache.avro.generic.GenericContainer;

/**
 * @author Infoblox Inc.
 *
 */
public interface IProtocolContainer <External, Internal>{
   public void init();
   public IProtocolStructure <External, Internal> structure(String structureName, byte []... in);
   public IProtocolAtom <External, Internal> atom(String atomName, byte []... in);
//   public IProtocolAtom<External, Internal> atom(String atomName, Internal... in);
   byte version();
}
