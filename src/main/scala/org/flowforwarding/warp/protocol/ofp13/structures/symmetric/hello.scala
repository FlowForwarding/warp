package org.flowforwarding.warp.protocol.ofp13.structures

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_hello_elem_type.OFP_HELLO_ELEM_TYPE

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_hello.ofp_hello_elem
import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import ofp_length._

/* OFPT_HELLO. This message includes zero or more hello elements having
 * variable size. Unknown elements types must be ignored/skipped, to allow
 * for future extensions. */
case class ofp_hello private[protocol] (header: ofp_header, elements: RawSeq[ofp_hello_elem])/* List of hello elements - 0 or more */ // TODO: skip unrecognized headers

object ofp_hello extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 1 => bodyLength }

  trait ofp_hello_elem extends WordTaggedUnion[singletonUnion[ofp_hello_elem_versionbitmap], OFP_HELLO_ELEM_TYPE]{
    def length: UInt16
    def intLength = UInt16.toShort(length)
  }

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32, elements: RawSeq[ofp_hello_elem] = RawSeq()) = {
    val header = ofp_header(OFPL_HELLO_LEN + elements.map { _.intLength }.sum, xid)
    ofp_hello(header, elements)
  }

  def apply(xid: Int, elements: ofp_hello_elem*): ofp_hello = build(UInt32.fromInt(xid), elements)
}

/* Hello elements types. */
object ofp_hello_elem_type extends WordEnum{
  type OFP_HELLO_ELEM_TYPE = Value
  val OFPHET_VERSIONBITMAP = ##(1) /* Bitmap of version supported. */
}

/* Common header for all Hello Elements */
case class ofp_hello_elem_header(hType: OFP_HELLO_ELEM_TYPE, /* One of OFPHET_*. */
                                 length: UInt16)             /* Length in bytes of the element, including this header, excluding padding. */

/* Version bitmap Hello Element */
case class ofp_hello_elem_versionbitmap private[protocol] (//hType: OFP_HELLO_ELEM_TYPE = OFPHET_VERSIONBITMAP, /* One of OFPHET_*. */
                                        length: UInt16,             /* Length in bytes of the element, including this header, excluding padding. */
                                        /* Followed by:
                                         * - Exactly (length - 4) bytes containing the bitmaps, then
                                         * - Exactly (length + 7)/8*8 - (length) (between 0 and 7)
                                         * bytes of all-zero bytes */
                                        bitmaps: RawSeq[UInt32],
                                        pad: RawSeq[Pad1]) extends ofp_hello.ofp_hello_elem/* List of bitmaps - supported versions */

object ofp_hello_elem_versionbitmap extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = {
    case 1 => s => lenField(0)(s) - 4
    case 2 => s =>  /* (lenField(1)(s) + 7) / 8 * 8 */ 0   // TODO: fix calculation
  }

  private[protocol] def build(bitmaps: RawSeq[UInt32] = RawSeq()) = {
    ofp_hello_elem_versionbitmap(UInt16.fromShort((4 + bitmaps.length * 4).toShort), bitmaps, RawSeq() /* fix length of padding*/)
  }

  def apply(bitmaps: Int*): ofp_hello_elem_versionbitmap = build(RawSeq(bitmaps map UInt32.fromInt: _*))
}
