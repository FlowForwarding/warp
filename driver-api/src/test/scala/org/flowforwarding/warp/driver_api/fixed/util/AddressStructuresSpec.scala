/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.util

import org.scalatest.{FlatSpec, Matchers}
import spire.math.{UInt, ULong}
import spire.syntax.literals._

class AddressStructuresSpec extends FlatSpec with Matchers {

  it should "construct ISIDs from Longs and Arrays of bytes " in {
    val fromLong = ISID(UInt(0x010203))
    val fromArray = ISID(Array[Byte](1, 2, 3))

    fromLong.bytes should equal { fromArray.bytes }

    fromLong.toLong should equal { fromArray.toLong }
  }

  it should "construct MacAddresses from Longs, Strings and Arrays of bytes " in {
    val fromLong = MacAddress(ULong(0x010203040506L))
    val fromArray = MacAddress(Array[Byte](1, 2, 3, 4, 5, 6))
    val fromString = MacAddress.parse("01:02:03:04:05:06")

    fromLong.bytes should equal { fromArray.bytes }
    fromLong.bytes should equal { fromString.get.bytes }

    fromLong.toLong should equal { fromArray.toLong }
    fromLong.toLong should equal { fromString.get.toLong }

    fromLong.toString should equal { "01:02:03:04:05:06" }
    fromArray.toString should equal { "01:02:03:04:05:06" }
    fromString.get.toString should equal { "01:02:03:04:05:06" }
  }

  it should "construct IPv4Addresses from Longs, Strings and Arrays of bytes " in {
    val fromLong = IPv4Address(UInt(0x7F000001))
    val fromArray = IPv4Address(Array[Byte](127, 0, 0, 1))
    val fromString = IPv4Address.parse("127.0.0.1")

    fromLong.bytes should equal { fromArray.bytes }
    fromLong.bytes should equal { fromString.get.bytes }

    fromLong.toLong should equal { fromArray.toLong }
    fromLong.toLong should equal { fromString.get.toLong }

    fromLong.toString should equal { "127.0.0.1" }
    fromArray.toString should equal { "127.0.0.1" }
  }

  it should "construct IPv6Addresses from Strings and Arrays of bytes " in {
    val fromArray = IPv6Address(Array(0xfe, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0xf8, 0xff, 0xfe, 0x21, 0x67, 0xcf) map (_.toByte))
    val fromString = IPv6Address.parse("fe80:0:0:0:200:f8ff:fe21:67cf")

    fromArray.bytes should equal { fromString.get.bytes }

    fromArray.toString should equal { "fe80:0:0:0:200:f8ff:fe21:67cf" }
  }
}