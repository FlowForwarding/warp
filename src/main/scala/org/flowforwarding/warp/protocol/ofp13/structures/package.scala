package org.flowforwarding.warp.protocol.ofp13

import java.nio.ByteOrder

import com.gensler.scalavro.util.U16
import com.gensler.scalavro.types.supply.UInt16

package object structures {

  /* Helper functions for deserialization of raw sequences */

  val bodyLength: Seq[Any] => Int = bodyLengthMinus(0)

  def bodyLengthMinus(size: Int): Seq[Any] => Int = {
    case Seq(h: ofp_header, _*) =>
      val headerSize = 1 + 1 + 2 + 4
      U16.f(h.length.data) - headerSize - size
  }

  val lenField: Int => Seq[Any] => Int = i => s => UInt16.toShort(s(i).asInstanceOf[UInt16])
}
