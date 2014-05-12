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

      result |=  ((long)(buffer[7])  & 255);
      result |=  (((long)(buffer[6])  & 255) << 8);
      result |=  (((long)(buffer[5])  & 255) << 16);
      result |=  (((long)(buffer[4])  & 255) << 24);
      result |=  (((long)(buffer[3])  & 255) << 32);
      result |=  (((long)(buffer[2])  & 255) << 40);
      result |=  (((long)(buffer[1])  & 255) << 48);
      result |=  (((long)(buffer[0])  & 255) << 56);

		return new Long(result);
   }
   
   public static String toHexString (byte[] buffer) {
      String res = new String();
      for (byte b : buffer) {
         res.concat(Integer.toHexString(b));
      }
      
      return res;
   }

}
