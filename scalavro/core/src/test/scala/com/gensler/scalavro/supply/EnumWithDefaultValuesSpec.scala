package com.gensler.scalavro.supply.test


import com.gensler.scalavro.test.AvroSpec
import com.gensler.scalavro.types.supply.{ByteEnum, WordEnum, DWordEnum, EnumWithDefaultValues}

class EnumWithDefaultValuesSpec extends AvroSpec {

  "EnumWithDefaultValues" should "be able to enumerate its members" in {
    object Test extends EnumWithDefaultValues[Int]{
      val f1 = value(1)
      val f2 = value(2)
      val f3 = value(3)
    }
    Test.values should equal (Map("f1" -> Test.f1, "f2" -> Test.f2, "f3" -> Test.f3))
  }

  it should "allow to define members with the same default value" in {
    new EnumWithDefaultValues[Int]{
      val f1 = value(1)
      val f2 = value(1)
      val f3 = value(3)
    }
  }

  "Unsigned fixed-size enums" should "not allow to define values lesser then zero and larger then the fixed size" in {
    an [IllegalArgumentException] should be thrownBy { new ByteEnum  { val f = ##(0xff + 1) } }
    an [IllegalArgumentException] should be thrownBy { new ByteEnum  { val f = ##(-1) } }
    an [IllegalArgumentException] should be thrownBy { new WordEnum  { val f = ##(0xffff + 1) } }
    an [IllegalArgumentException] should be thrownBy { new WordEnum  { val f = ##(-1) } }
    an [IllegalArgumentException] should be thrownBy { new DWordEnum { val f = ##(0xffffffffL + 1) } }
    an [IllegalArgumentException] should be thrownBy { new DWordEnum { val f = ##(-1) } }

    /* no Exception should be thrownBy */ { new ByteEnum  { val f = ##(0xff) } }
    /* no Exception should be thrownBy */ { new WordEnum  { val f = ##(0xffff) } }
    /* no Exception should be thrownBy */ { new DWordEnum { val f = ##(0xffffffff) } }
  }
}