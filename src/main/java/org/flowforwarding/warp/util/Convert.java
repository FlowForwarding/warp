/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.util;

/**
 * @author Infoblox Inc.
 *
 */
public class Convert {
   
   public static Long toLong(byte[] buffer) {
      long result = 0;
		for (int i=0; i<8; i++)
		   result = (result | buffer[i]) << 8; 
		return new Long(result);
   }

}
