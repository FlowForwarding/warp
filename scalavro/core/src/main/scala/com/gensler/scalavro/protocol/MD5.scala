package com.gensler.scalavro.protocol

import com.gensler.scalavro.util.FixedData
import scala.collection.immutable

@FixedData.Length(16)
case class MD5(override val bytes: immutable.Seq[Byte])
  extends FixedData(bytes)
