/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.codehaus.jackson.node.NullNode;

import org.apache.avro.Schema.Field;
import org.apache.avro.reflect.TestReflect.SampleRecord.AnotherSampleRecord;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.generic.GenericData;

import org.junit.Test;

public class TestReflect {
  
  EncoderFactory factory = new EncoderFactory();
  
  // test primitive type inference
  @Test public void testVoid() {
    check(Void.TYPE, "\"null\"");
    check(Void.class, "\"null\"");
  }

  @Test public void testBoolean() {
    check(Boolean.TYPE, "\"boolean\"");
    check(Boolean.class, "\"boolean\"");
  }

  @Test public void testInt() {
    check(Integer.TYPE, "\"int\"");
    check(Integer.class, "\"int\"");
  }

  @Test public void testByte() {
    check(Byte.TYPE, "{\"type\":\"int\",\"java-class\":\"java.lang.Byte\"}");
    check(Byte.class, "{\"type\":\"int\",\"java-class\":\"java.lang.Byte\"}");
  }

  @Test public void testShort() {
    check(Short.TYPE, "{\"type\":\"int\",\"java-class\":\"java.lang.Short\"}");
    check(Short.class, "{\"type\":\"int\",\"java-class\":\"java.lang.Short\"}");
  }

  @Test public void testLong() {
    check(Long.TYPE, "\"long\"");
    check(Long.class, "\"long\"");
  }

  @Test public void testFloat() {
    check(Float.TYPE, "\"float\"");
    check(Float.class, "\"float\"");
  }

  @Test public void testDouble() {
    check(Double.TYPE, "\"double\"");
    check(Double.class, "\"double\"");
  }

  @Test public void testString() {
    check("Foo", "\"string\"");
  }

  @Test public void testBytes() {
    check(ByteBuffer.allocate(0), "\"bytes\"");
    check(new byte[0], "{\"type\":\"bytes\",\"java-class\":\"[B\"}");
  }

  @Test public void testUnionWithCollection() {
    Schema s = Schema.parse
      ("[\"null\", {\"type\":\"array\",\"items\":\"float\"}]");
    GenericData data = ReflectData.get();
    assertEquals(1, data.resolveUnion(s, new ArrayList<Float>()));
  }

  @Test public void testUnionWithMap() {
    Schema s = Schema.parse
      ("[\"null\", {\"type\":\"map\",\"values\":\"float\"}]");
    GenericData data = ReflectData.get();
    assertEquals(1, data.resolveUnion(s, new HashMap<String,Float>()));
  }

  @Test public void testUnionWithBytes() {
    Schema s = Schema.parse ("[\"null\", \"bytes\"]");
    GenericData data = ReflectData.get();
    assertEquals(1, data.resolveUnion(s, ByteBuffer.wrap(new byte[]{1})));
  }

  // test map, array and list type inference
  public static class R1 {
    private Map<String,String> mapField = new HashMap<String,String>();
    private String[] arrayField = new String[] { "foo" };
    private List<String> listField = new ArrayList<String>();

    {
      mapField.put("foo", "bar");
      listField.add("foo");
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof R1)) return false;
      R1 that = (R1)o;
      return mapField.equals(that.mapField)
        && Arrays.equals(this.arrayField, that.arrayField) 
        &&  listField.equals(that.listField);
    }
  }

  @Test public void testMap() throws Exception {
    check(R1.class.getDeclaredField("mapField").getGenericType(),
          "{\"type\":\"map\",\"values\":\"string\"}");
  }

  @Test public void testArray() throws Exception {
    check(R1.class.getDeclaredField("arrayField").getGenericType(),
          "{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"[Ljava.lang.String;\"}");
  }
  @Test public void testList() throws Exception {
    check(R1.class.getDeclaredField("listField").getGenericType(),
          "{\"type\":\"array\",\"items\":\"string\""
          +",\"java-class\":\"java.util.List\"}");
  }

  @Test public void testR1() throws Exception {
    checkReadWrite(new R1());
  }

  // test record, array and list i/o
  public static class R2 {
    private String[] arrayField;
    private Collection<String> collectionField;
    
    public boolean equals(Object o) {
      if (!(o instanceof R2)) return false;
      R2 that = (R2)o;
      return Arrays.equals(this.arrayField, that.arrayField) 
        &&  collectionField.equals(that.collectionField);
    }
  }

  @Test public void testR2() throws Exception {
    R2 r2 = new R2();
    r2.arrayField = new String[] {"foo"};
    r2.collectionField = new ArrayList<String>();
    r2.collectionField.add("foo");
    checkReadWrite(r2);
  }

  // test array i/o of unboxed type
  public static class R3 {
    private int[] intArray;
    
    public boolean equals(Object o) {
      if (!(o instanceof R3)) return false;
      R3 that = (R3)o;
      return Arrays.equals(this.intArray, that.intArray);
    }
  }

  @Test public void testR3() throws Exception {
    R3 r3 = new R3();
    r3.intArray = new int[] {1};
    checkReadWrite(r3);
  }

  // test inherited fields & short datatype
  public static class R4 {
    public short value;
    public short[] shorts;
    public byte b;
    
    public boolean equals(Object o) {
      if (!(o instanceof R4)) return false;
      R4 that = (R4)o;
      return this.value == that.value
        && Arrays.equals(this.shorts, that.shorts)
        && this.b == that.b;
    }
  }

  public static class R5 extends R4 {}

  @Test public void testR5() throws Exception {
    R5 r5 = new R5();
    r5.value = 1;
    r5.shorts = new short[] {3,255,256,Short.MAX_VALUE,Short.MIN_VALUE};
    r5.b = 99;
    checkReadWrite(r5);
  }

  // test union annotation on a class
  @Union({R7.class, R8.class})
  public static class R6 {}

  public static class R7 extends R6 {
    public int value;
    public boolean equals(Object o) {
      if (!(o instanceof R7)) return false;
      return this.value == ((R7)o).value;
    }
  }
  public static class R8 extends R6 {
    public float value;
    public boolean equals(Object o) {
      if (!(o instanceof R8)) return false;
      return this.value == ((R8)o).value;
    }
  }

  // test arrays of union annotated class
  public static class R9  {
    public R6[] r6s;
    public boolean equals(Object o) {
      if (!(o instanceof R9)) return false;
      return Arrays.equals(this.r6s, ((R9)o).r6s);
    }
  }

  @Test public void testR6() throws Exception {
    R7 r7 = new R7();
    r7.value = 1;
    checkReadWrite(r7, ReflectData.get().getSchema(R6.class));
    R8 r8 = new R8();
    r8.value = 1;
    checkReadWrite(r8, ReflectData.get().getSchema(R6.class));
    R9 r9 = new R9();
    r9.r6s = new R6[] {r7, r8};
    checkReadWrite(r9, ReflectData.get().getSchema(R9.class));
  }

  // test union annotation on methods and parameters
  public static interface P0 {
    @Union({Void.class,String.class})
      String foo(@Union({Void.class,String.class}) String s);
  }

  @Test public void testP0() throws Exception {
    Protocol p0 = ReflectData.get().getProtocol(P0.class);
    Protocol.Message message = p0.getMessages().get("foo");
    // check response schema is union
    Schema response = message.getResponse();
    assertEquals(Schema.Type.UNION, response.getType());
    assertEquals(Schema.Type.NULL, response.getTypes().get(0).getType());
    assertEquals(Schema.Type.STRING, response.getTypes().get(1).getType());
    // check request schema is union
    Schema request = message.getRequest();
    Field field = request.getField("s");
    assertNotNull("field 's' should not be null", field);
    Schema param = field.schema();
    assertEquals(Schema.Type.UNION, param.getType());
    assertEquals(Schema.Type.NULL, param.getTypes().get(0).getType());
    assertEquals(Schema.Type.STRING, param.getTypes().get(1).getType());
    // check union erasure
    assertEquals(String.class, ReflectData.get().getClass(response));
    assertEquals(String.class, ReflectData.get().getClass(param));
  }

  // test Stringable annotation
  @Stringable public static class R10 {
    private String text;
    public R10(String text) { this.text = text; }
    public String toString() { return text; }
    public boolean equals(Object o) {
      if (!(o instanceof R10)) return false;
      return this.text.equals(((R10)o).text);
    }
  }
  
  @Test public void testR10() throws Exception {
    Schema r10Schema = ReflectData.get().getSchema(R10.class);
    assertEquals(Schema.Type.STRING, r10Schema.getType());
    assertEquals(R10.class.getName(), r10Schema.getProp("java-class"));
    checkReadWrite(new R10("foo"), r10Schema);
  }

  // test Nullable annotation on field
  public static class R11 {
    @Nullable private String text;
    public boolean equals(Object o) {
      if (!(o instanceof R11)) return false;
      R11 that = (R11)o;
      if (this.text == null) return that.text == null;
      return this.text.equals(that.text);
    }
  }
  
  @Test public void testR11() throws Exception {
    Schema r11Record = ReflectData.get().getSchema(R11.class);
    assertEquals(Schema.Type.RECORD, r11Record.getType());
    Field r11Field = r11Record.getField("text");
    assertEquals(NullNode.getInstance(), r11Field.defaultValue());
    Schema r11FieldSchema = r11Field.schema();
    assertEquals(Schema.Type.UNION, r11FieldSchema.getType());
    assertEquals(Schema.Type.NULL, r11FieldSchema.getTypes().get(0).getType());
    Schema r11String = r11FieldSchema.getTypes().get(1);
    assertEquals(Schema.Type.STRING, r11String.getType());
    R11 r11 = new R11();
    checkReadWrite(r11, r11Record);
    r11.text = "foo";
    checkReadWrite(r11, r11Record);
  }

  // test nullable annotation on methods and parameters
  public static interface P1 {
    @Nullable String foo(@Nullable String s);
  }

  @Test public void testP1() throws Exception {
    Protocol p1 = ReflectData.get().getProtocol(P1.class);
    Protocol.Message message = p1.getMessages().get("foo");
    // check response schema is union
    Schema response = message.getResponse();
    assertEquals(Schema.Type.UNION, response.getType());
    assertEquals(Schema.Type.NULL, response.getTypes().get(0).getType());
    assertEquals(Schema.Type.STRING, response.getTypes().get(1).getType());
    // check request schema is union
    Schema request = message.getRequest();
    Field field = request.getField("s");
    assertNotNull("field 's' should not be null", field);
    Schema param = field.schema();
    assertEquals(Schema.Type.UNION, param.getType());
    assertEquals(Schema.Type.NULL, param.getTypes().get(0).getType());
    assertEquals(Schema.Type.STRING, param.getTypes().get(1).getType());
    // check union erasure
    assertEquals(String.class, ReflectData.get().getClass(response));
    assertEquals(String.class, ReflectData.get().getClass(param));
  }

  // test error
  public static class E1 extends Exception {}
  public static interface P2 {
    void error() throws E1;
  }

  @Test public void testP2() throws Exception {
    Schema e1 = ReflectData.get().getSchema(E1.class);
    assertEquals(Schema.Type.RECORD, e1.getType());
    assertTrue(e1.isError());
    Field message = e1.getField("detailMessage");
    assertNotNull("field 'detailMessage' should not be null", message);
    Schema messageSchema = message.schema();
    assertEquals(Schema.Type.UNION, messageSchema.getType());
    assertEquals(Schema.Type.NULL, messageSchema.getTypes().get(0).getType());
    assertEquals(Schema.Type.STRING, messageSchema.getTypes().get(1).getType());

    Protocol p2 = ReflectData.get().getProtocol(P2.class);
    Protocol.Message m = p2.getMessages().get("error");
    // check error schema is union
    Schema response = m.getErrors();
    assertEquals(Schema.Type.UNION, response.getType());
    assertEquals(Schema.Type.STRING, response.getTypes().get(0).getType());
    assertEquals(e1, response.getTypes().get(1));
  }

  @Test public void testNoPackage() throws Exception {
    Class noPackage = Class.forName("NoPackage");
    Schema s = ReflectData.get().getSchema(noPackage);
    assertEquals(noPackage.getName(), ReflectData.get().getClassName(s));
  }

  void checkReadWrite(Object object) throws Exception {
    checkReadWrite(object, ReflectData.get().getSchema(object.getClass()));
  }
  void checkReadWrite(Object object, Schema s) throws Exception {
    ReflectDatumWriter<Object> writer = new ReflectDatumWriter<Object>(s);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writer.write(object, factory.directBinaryEncoder(out, null));
    ReflectDatumReader<Object> reader = new ReflectDatumReader<Object>(s);
    Object after =
      reader.read(null, DecoderFactory.get().binaryDecoder(
          out.toByteArray(), null));
    assertEquals(object, after);
  }

  public static enum E { A, B };
  @Test public void testEnum() throws Exception {
    check(E.class, "{\"type\":\"enum\",\"name\":\"E\",\"namespace\":"
          +"\"org.apache.avro.reflect.TestReflect$\",\"symbols\":[\"A\",\"B\"]}");
  }

  public static class R { int a; long b; }
  @Test public void testRecord() throws Exception {
    check(R.class, "{\"type\":\"record\",\"name\":\"R\",\"namespace\":"
          +"\"org.apache.avro.reflect.TestReflect$\",\"fields\":["
          +"{\"name\":\"a\",\"type\":\"int\"},"
          +"{\"name\":\"b\",\"type\":\"long\"}]}");
  }

  private void check(Object o, String schemaJson) {
    check(o.getClass(), schemaJson);
  }

  private void check(Type type, String schemaJson) {
    assertEquals(schemaJson, ReflectData.get().getSchema(type).toString());
  }

  @Test
  public void testRecordIO() throws IOException {
    Schema schm = ReflectData.get().getSchema(SampleRecord.class);
    ReflectDatumWriter<SampleRecord> writer = 
      new ReflectDatumWriter<SampleRecord>(schm);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    SampleRecord record = new SampleRecord();
    record.x = 5;
    record.y = 10;
    writer.write(record, factory.directBinaryEncoder(out, null));
    ReflectDatumReader<SampleRecord> reader = 
      new ReflectDatumReader<SampleRecord>(schm);
    SampleRecord decoded =
      reader.read(null, DecoderFactory.get().binaryDecoder(
          out.toByteArray(), null));
    assertEquals(record, decoded);
  }

  @Test
  public void testRecordWithNullIO() throws IOException {
    ReflectData reflectData = ReflectData.AllowNull.get();
    Schema schm = reflectData.getSchema(AnotherSampleRecord.class);
    ReflectDatumWriter<AnotherSampleRecord> writer = 
      new ReflectDatumWriter<AnotherSampleRecord>(schm);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    // keep record.a null and see if that works
    Encoder e = factory.directBinaryEncoder(out, null);
    AnotherSampleRecord a = new AnotherSampleRecord();
    writer.write(a, e);
    AnotherSampleRecord b = new AnotherSampleRecord(10);
    writer.write(b, e);
    e.flush();
    ReflectDatumReader<AnotherSampleRecord> reader = 
      new ReflectDatumReader<AnotherSampleRecord>(schm);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Decoder d = DecoderFactory.get().binaryDecoder(in, null);
    AnotherSampleRecord decoded = reader.read(null, d);
    assertEquals(a, decoded);
    decoded = reader.read(null, d);
    assertEquals(b, decoded);
  }

  public static class SampleRecord {
    public int x = 1;
    private int y = 2;

    public int hashCode() {
      return x + y;
    }

    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final SampleRecord other = (SampleRecord)obj;
      if (x != other.x)
        return false;
      if (y != other.y)
        return false;
      return true;
    }
    
    public static class AnotherSampleRecord {
      private Integer a = null;
      private SampleRecord s = null;

      public AnotherSampleRecord() {
      }

      AnotherSampleRecord(Integer a) {
        this.a = a;
        this.s = new SampleRecord();
      }

      public int hashCode() {
        int hash = (a != null ? a.hashCode() : 0);
        hash += (s != null ? s.hashCode() : 0);
        return hash;
      }

      public boolean equals(Object other) {
        if (other instanceof AnotherSampleRecord) {
          AnotherSampleRecord o = (AnotherSampleRecord) other;
          if ( (this.a == null && o.a != null) ||
               (this.a != null && !this.a.equals(o.a)) ||
               (this.s == null && o.s != null) ||
               (this.s != null && !this.s.equals(o.s)) ) {
            return false;
          }
          return true;
        } else {
          return false;
        }
      }
    }
  }

  public static class X { int i; }
  public static class B1 { X x; }
  public static class B2 { X x; }
  public static class A { B1 b1; B2 b2; }
  public static interface C { void foo(A a); }

  @Test
  public void testForwardReference() {
    ReflectData data = ReflectData.get();
    Protocol reflected = data.getProtocol(C.class);
    Protocol reparsed = Protocol.parse(reflected.toString());
    assertEquals(reflected, reparsed);
    assert(reparsed.getTypes().contains(data.getSchema(A.class)));
    assert(reparsed.getTypes().contains(data.getSchema(B1.class)));
    assert(reparsed.getTypes().contains(data.getSchema(B2.class)));
    assert(reparsed.getTypes().contains(data.getSchema(X.class)));
  }

  public static interface P3 {
    void m1();
    void m1(int x);
  }

  @Test(expected=AvroTypeException.class)
  public void testOverloadedMethod() { 
    ReflectData.get().getProtocol(P3.class);
  }

  @Test
  public void testNoPackageSchema() throws Exception {
    ReflectData.get().getSchema(Class.forName("NoPackage"));
  }

  @Test
  public void testNoPackageProtocol() throws Exception {
    ReflectData.get().getProtocol(Class.forName("NoPackage"));
  }

  public static class Y {
    int i;
  }

  @Test
  /** Test nesting of reflect data within generic. */
  public void testReflectWithinGeneric() throws Exception {
    ReflectData data = ReflectData.get();
    // define a record with a field that's a specific Y
    Schema schema = Schema.createRecord("Foo", "", "x.y.z", false);
    List<Schema.Field> fields = new ArrayList<Schema.Field>();
    fields.add(new Schema.Field("f", data.getSchema(Y.class), "", null));
    schema.setFields(fields);

    // create a generic instance of this record
    Y y = new Y();
    y.i = 1;
    GenericData.Record record = new GenericData.Record(schema);
    record.put("f", y);

    // test that this instance can be written & re-read
    checkBinary(schema, record);
  }

  /** Test union of null and an array. */
  @Test
  public void testNullArray() throws Exception {
    String json = "[{\"type\":\"array\", \"items\": \"long\"}, \"null\"]";
    Schema schema = Schema.parse(json);
    checkBinary(schema, null);
  }

  /** Test stringable classes. */
  @Test public void testStringables() throws Exception {
    checkStringable(java.math.BigDecimal.class, "10");
    checkStringable(java.math.BigInteger.class, "20");
    checkStringable(java.net.URI.class, "foo://bar:9000/baz");
    checkStringable(java.net.URL.class, "http://bar:9000/baz");
    checkStringable(java.io.File.class, "foo.bar");
  }

  public void checkStringable(Class c, String value) throws Exception {
    ReflectData data = new ReflectData().get();
    Schema schema = data.getSchema(c);
    assertEquals
      ("{\"type\":\"string\",\"java-class\":\""+c.getName()+"\"}",
       schema.toString());
    checkBinary(schema, c.getConstructor(String.class).newInstance(value));
  }

  public static class M1 {
    Map<Integer, String> integerKeyMap;
    Map<java.math.BigInteger, String> bigIntegerKeyMap;
    Map<java.math.BigDecimal, String> bigDecimalKeyMap;
    Map<java.io.File, String> fileKeyMap;
  }

  /** Test Map with stringable key classes. */
  @Test public void testStringableMapKeys() throws Exception {
    M1 record = new M1();
    record.integerKeyMap = new HashMap<Integer, String>(1);
    record.integerKeyMap.put(10, "foo");

    record.bigIntegerKeyMap = new HashMap<java.math.BigInteger, String>(1);
    record.bigIntegerKeyMap.put(java.math.BigInteger.TEN, "bar");

    record.bigDecimalKeyMap = new HashMap<java.math.BigDecimal, String>(1);
    record.bigDecimalKeyMap.put(java.math.BigDecimal.ONE, "bigDecimal");

    record.fileKeyMap = new HashMap<java.io.File, String>(1);
    record.fileKeyMap.put(new java.io.File("foo.bar"), "file");

    ReflectData data = new ReflectData().addStringable(Integer.class);

    checkBinary(data, data.getSchema(M1.class), record, true);
  }

  public static class NullableStringable {
    java.math.BigDecimal number;
  }

  @Test public void testNullableStringableField() throws Exception {
    NullableStringable datum = new NullableStringable();
    datum.number = java.math.BigDecimal.TEN;

    Schema schema = ReflectData.AllowNull.get().getSchema(NullableStringable.class);
    checkBinary(schema, datum);
  }

  public static void checkBinary(ReflectData reflectData, Schema schema,
                                 Object datum, boolean equals)
    throws IOException {
    ReflectDatumWriter<Object> writer = new ReflectDatumWriter<Object>(schema);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writer.write(datum, EncoderFactory.get().directBinaryEncoder(out, null));
    byte[] data = out.toByteArray();

    ReflectDatumReader<Object> reader = new ReflectDatumReader<Object>(schema);
    Object decoded =
      reader.read(null, DecoderFactory.get().binaryDecoder(
          data, null));

    assertEquals(0, reflectData.compare(datum, decoded, schema, equals));
  }

  public static void checkBinary(Schema schema, Object datum)
    throws IOException {
    checkBinary(ReflectData.get(), schema, datum, false);
  }

  /** Test that the error message contains the name of the class. */
  @Test
  public void testReflectFieldError() throws Exception {
    Object datum = "";
    try {
      ReflectData.get().getField(datum, "notAFieldOfString", 0);
    } catch (AvroRuntimeException e) {
      assertTrue(e.getMessage().contains(datum.getClass().getName()));
    }
  }


}
