/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api

object XidGenerator {
  private val xidGen = new java.util.concurrent.atomic.AtomicLong()
  private val MAX_XID = 0xFFFFFFFFL
  private val lock = new AnyRef

  def nextXid() = {
    var xid: Long = 0
    do {
      xid = xidGen.incrementAndGet()
      if(xid > MAX_XID)
        lock synchronized {
          if(xidGen.get() > MAX_XID) xidGen.set(0)
        }
    } while(xid > MAX_XID)
    xid
  }
}