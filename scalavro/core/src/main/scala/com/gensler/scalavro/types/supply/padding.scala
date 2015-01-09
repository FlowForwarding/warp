package com.gensler.scalavro.types

import com.gensler.scalavro.util.FixedData
import java.nio.{ByteBuffer, ByteOrder}
import com.gensler.scalavro.util.Union.union

package object supply{
  type singletonUnion[T] = union[T] #or [Unit]
  private[scalavro] type Bytes = scala.collection.immutable.Seq[Byte]
  private[scalavro] def fixedToBuffer(value: FixedData, bo: ByteOrder) = ByteBuffer.wrap(value.bytes.toArray).order(bo)
}

package supply{
  import com.gensler.scalavro.util.FixedData

  @FixedData.Length(1)
  case class Pad1() extends FixedData(List[Byte](0)){
    private[scalavro] def this(bytes: Bytes) = this()
  }

  @FixedData.Length(2)
  case class Pad2() extends FixedData(List[Byte](0, 0)) {
    private[scalavro] def this(bytes: Bytes) = this()
  }

  @FixedData.Length(3)
  case class Pad3() extends FixedData(List[Byte](0, 0, 0)) {
    private[scalavro] def this(bytes: Bytes) = this()
  }

  @FixedData.Length(4)
  case class Pad4() extends FixedData(List[Byte](0, 0, 0, 0)){
    private[scalavro] def this(bytes: Bytes) = this()
  }

  @FixedData.Length(5)
  case class Pad5() extends FixedData(List[Byte](0, 0, 0, 0, 0)){
    private[scalavro] def this(bytes: Bytes) = this()
  }

  @FixedData.Length(6)
  case class Pad6() extends FixedData(List[Byte](0, 0, 0, 0, 0, 0)){
    private[scalavro] def this(bytes: Bytes) = this()
  }

  @FixedData.Length(7)
  case class Pad7() extends FixedData(List[Byte](0, 0, 0, 0, 0, 0)){
    private[scalavro] def this(bytes: Bytes) = this()
  }
}

