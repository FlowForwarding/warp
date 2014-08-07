package org.flowforwarding.warp.controller


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