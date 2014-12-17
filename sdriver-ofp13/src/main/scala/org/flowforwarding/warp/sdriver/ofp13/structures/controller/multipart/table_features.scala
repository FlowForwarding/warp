/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._

/* Body for ofp_multipart_request of type OFPMP_TABLE_FEATURES.
 * Body of reply to OFPMP_TABLE_FEATURES request. */
case class ofp_table_features private[sdriver] (
  length: UInt16, /* Length is padded to 64 bits. */
  table_id: UInt8, /* Identifier of table. Lower numbered tables are consulted first. */
  pad: Pad5, /* Align to 64-bits. */
  name: RawSeq[UInt8],
  metadata_match: UInt64, /* Bits of metadata table can match. */
  metadata_write: UInt64, /* Bits of metadata table can write. */
  config: UInt32, /* Bitmap of OFPTC_* values */
  max_entries: UInt32, /* Max number of entries supported. */
  properties: RawSeq[ofp_table_feature_prop] /* List of properties */
)

object ofp_table_features extends RawSeqFieldsInfo{
  private [sdriver] def build(table_id: UInt8,
                               name: RawSeq[UInt8],
                               metadata_match: UInt64,
                               metadata_write: UInt64,
                               config: UInt32,
                               max_entries: UInt32,
                               properties: RawSeq[ofp_table_feature_prop]) = {
    val len = 64 + properties.map(p => UInt16.toShort(p.len)).sum
    val nameBytes = name.take(32) ++ Seq.fill(Math.max(32 - name.length, 0)) { UInt8(0) }
    ofp_table_features(UInt16.fromShort(len.toShort),
      table_id,
      Pad5(),
      RawSeq(nameBytes : _*),
      metadata_match,
      metadata_write,
      config,
      max_entries,
      RawSeq(properties: _*))
  }

  def apply(table_id: UInt8,
            name: String,
            metadata_match: UInt64,
            metadata_write: UInt64,
            config: UInt32,
            max_entries: UInt32,
            properties: Array[ofp_table_feature_prop]): ofp_table_features = {
    val nameBytes = java.nio.ByteBuffer.allocate(32)
                                       .put(name.getBytes.take(32))
                                       .array()
                                       .map(UInt8.fromByte)
    build(table_id,
          RawSeq(nameBytes: _*),
          metadata_match,
          metadata_write,
          config,
          max_entries,
          RawSeq(properties: _*))
  }

  override val rawFieldsLengthCalculator: LengthCalculator = {
    case 3 => Some { _ => 32 }
    case 8 => Some { (s: Seq[Any]) => lenField(0)(s) - 64 }
  }
}

case class ofp_multipart_table_features_request(features: Array[ofp_table_features]) extends MultipartRequest[ofp_table_features] {
  override def tp: OFP_MULTIPART_TYPE =  ofp_multipart_type.OFPMP_TABLE_FEATURES

  override def structures = features
}

object ofp_multipart_table_features_request {
  private [sdriver] def build(structures: RawSeq[ofp_table_features]) = ofp_multipart_table_features_request(structures.toArray)
}

case class ofp_multipart_table_features_reply private [sdriver] (features: RawSeq[ofp_table_features])