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
   
   public static void main(String[] args) {
      toArray("0x567778");
      toArray("0x56777D");
      toArray("0x56777d");
      toArray("56777D");
      toArray("567770");
   }
   
   public static long toLong(byte[] buffer) {
      //TODO Improvs: Throw an exception in case size is more than 8?
      long result = 0;
      int shift = 8 * (buffer.length - 1);
      
      for (byte b : buffer) {
         result |= ((long)(b & 255)) << shift;
         shift -= 8;
      }
      
		return result;
   }
   
   public static String toHexString (byte[] buffer) {
      String res = new String();
      for (byte b : buffer) {
         res.concat(Integer.toHexString(b));
      }
      
      return res;
   }
   
   public static byte[] toArray (String in) {
      
      String patternHex = "(?:0[xX])?[0-9a-fA-F]+";
      String patternDec = "(?:0[dD])?[0-9]+";
      
      Matcher matchDec = Pattern.compile(patternDec).matcher(in);
      if (matchDec.matches()) {
         System.out.println("Decimal");
         return null;
      }

      Matcher matchHex = Pattern.compile(patternHex).matcher(in);
      if (matchHex.matches()) {
         System.out.println("Hexadecimal");
         return null;
      }
      
      return null;
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
