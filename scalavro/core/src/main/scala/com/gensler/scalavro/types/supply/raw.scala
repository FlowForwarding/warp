package com.gensler.scalavro.types.supply

/* Companion objects of each record which has fields of type RawSeq has to implement this trait.
   Otherwise it couldn't be used as AvroType.
 */
trait RawSeqFieldsInfo{
  /* Returns length in bytes of raw sequence field by its number and already deserialized fields */
  type LengthCalculator = PartialFunction[Int, Seq[Any] => Int]
  val rawFieldsLengthCalculator: LengthCalculator
  protected def lenByIndex(index: Int) = (s: Seq[Any]) => s(index).asInstanceOf[Int]
}

trait RawData

sealed trait RawSeq[T] extends Seq[T] with RawData

object RawSeq{
  implicit def seq2RawSeq[T](seq: Seq[T]): RawSeq[T] = {
    val underlying = seq
    new RawSeq[T] {
      def apply(idx: Int): T = underlying(idx)

      def iterator: Iterator[T] = underlying.iterator

      def length: Int = underlying.length
    }
  }
  def apply[T](elems: T*): RawSeq[T] = Seq(elems: _*)
}
