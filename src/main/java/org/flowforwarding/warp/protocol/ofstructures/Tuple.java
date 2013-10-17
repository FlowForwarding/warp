/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofstructures;

/**
 * @author Infoblox Inc.
 * @param <N>
 * @param <V>
 *
 */
public class Tuple <N, V> {
   protected final N name;
   protected final V value;
   
   public Tuple (N n, V v) {
      name = n;
      value = v;
   }
   
   public N getName() {
      return name;
   }
   
   public V getValue() {
      return value;
   }
}
