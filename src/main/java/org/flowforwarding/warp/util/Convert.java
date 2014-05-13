/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
   
   public static byte[] dpidToBytes (String id) {
      
      byte[] dpid = new byte[8];
//      Matcher matcher = Pattern.compile("^([0-9A-Fa-f]{2}[\\.:-]){7}([0-9A-Fa-f]{2})$").matcher(id);
//      Matcher matcher = Pattern.compile("^([0-9A-Fa-f]{2}[\\.:-]){7}([0-9A-Fa-f]{2})$").matcher(id);      
//      Matcher matcher = Pattern.compile("^([0-9A-F]{2}[:-]){7}([0-9A-F]{2})$").matcher(id);
      Matcher matcher = Pattern.compile("(?:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+)\\:(\\p{XDigit}+):(\\p{XDigit}+):(\\p{XDigit}+))").matcher(id);
      
      if (matcher.matches()) {
         for (int i=0; i<8; i++) {
            if (matcher.group(i+1) != null) {
               try {
                  dpid[i] = getByte("0x" + matcher.group(i+1));
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }
            else {
               return null;
            }
        }
      } else
         return null;

      return dpid;
   }
   
   private static byte getByte(String str) {
      return Integer.decode(str).byteValue();
  }
}
