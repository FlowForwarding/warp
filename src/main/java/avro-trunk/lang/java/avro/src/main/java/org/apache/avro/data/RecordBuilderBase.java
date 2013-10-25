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
package org.apache.avro.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.parsing.ResolvingGrammarGenerator;
import org.codehaus.jackson.JsonNode;

/** Abstract base class for RecordBuilder implementations.  Not thread-safe. */
public abstract class RecordBuilderBase<T extends IndexedRecord> 
  implements RecordBuilder<T> {
  private static final ConcurrentMap<String, ConcurrentMap<Integer, Object>> 
    DEFAULT_VALUE_CACHE = 
      new ConcurrentHashMap<String, ConcurrentMap<Integer, Object>>();
  private static final Field[] EMPTY_FIELDS = new Field[0];
  private final Schema schema;
  private final Field[] fields;
  private final boolean[] fieldSetFlags;
  private final GenericData data;
  private BinaryEncoder encoder = null;
  private BinaryDecoder decoder = null;
  
  protected final Schema schema() { return schema; }
  protected final Field[] fields() { return fields; }
  protected final boolean[] fieldSetFlags() { return fieldSetFlags; }
  protected final GenericData data() { return data; }

  /**
   * Creates a RecordBuilderBase for building records of the given type.
   * @param schema the schema associated with the record class.
   */
  protected RecordBuilderBase(Schema schema, GenericData data) {
    this.schema = schema;
    this.data = data;
    fields = (Field[]) schema.getFields().toArray(EMPTY_FIELDS);
    fieldSetFlags = new boolean[fields.length];
  }
  
  /**
   * RecordBuilderBase copy constructor.
   * Makes a deep copy of the values in the other builder.
   * @param other RecordBuilderBase instance to copy.
   */
  protected RecordBuilderBase(RecordBuilderBase<T> other, GenericData data) {
    this.schema = other.schema;
    this.data = data;
    fields = (Field[]) schema.getFields().toArray(EMPTY_FIELDS);
    fieldSetFlags = new boolean[other.fieldSetFlags.length];
    System.arraycopy(
        other.fieldSetFlags, 0, fieldSetFlags, 0, fieldSetFlags.length);
  }
  
  /**
   * Validates that a particular value for a given field is valid according to 
   * the following algorithm:
   * 1. If the value is not null, or the field type is null, or the field type 
   * is a union which accepts nulls, returns.
   * 2. Else, if the field has a default value, returns.
   * 3. Otherwise throws AvroRuntimeException. 
   * @param field the field to validate.
   * @param value the value to validate.
   * @throws NullPointerException if value is null and the given field does 
   * not accept null values.
   */
  protected void validate(Field field, Object value) {
    if (isValidValue(field, value)) {
      return;
    }
    else if (field.defaultValue() != null) {
      return;
    }
    else {
      throw new AvroRuntimeException(
          "Field " + field + " does not accept null values");
    }
  }

  /**
   * Tests whether a value is valid for a specified field. 
   * @param f the field for which to test the value.
   * @param value the value to test.
   * @return true if the value is valid for the given field; false otherwise.
   */
  protected static boolean isValidValue(Field f, Object value) {
    if (value != null) {
      return true;
    }
    
    Schema schema = f.schema();
    Type type = schema.getType();
    
    // If the type is null, any value is valid
    if (type == Type.NULL) {
      return true;
    }

    // If the type is a union that allows nulls, any value is valid
    if (type == Type.UNION) {
      for (Schema s : schema.getTypes()) {
        if (s.getType() == Type.NULL) {
          return true;
        }
      }
    }
    
    // The value is null but the type does not allow nulls
    return false;
  }
  
  /**
   * Gets the default value of the given field, if any.
   * @param field the field whose default value should be retrieved.
   * @return the default value associated with the given field, 
   * or null if none is specified in the schema.
   * @throws IOException 
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Object defaultValue(Field field) throws IOException {    
    JsonNode defaultJsonValue = field.defaultValue();
    if (defaultJsonValue == null) {
      throw new AvroRuntimeException("Field " + field + " not set and has no default value");
    }
    if (defaultJsonValue.isNull()
        && (field.schema().getType() == Type.NULL
            || (field.schema().getType() == Type.UNION
                && field.schema().getTypes().get(0).getType() == Type.NULL))) {
      return null;
    }
    
    // Get the default value
    Object defaultValue = null;
    
    // First try to get the default value from cache:
    ConcurrentMap<Integer, Object> defaultSchemaValues = 
      DEFAULT_VALUE_CACHE.get(schema.getFullName());
    if (defaultSchemaValues == null) {
      DEFAULT_VALUE_CACHE.putIfAbsent(schema.getFullName(), 
          new ConcurrentHashMap<Integer, Object>(fields.length));
      defaultSchemaValues = DEFAULT_VALUE_CACHE.get(schema.getFullName());
    }
    defaultValue = defaultSchemaValues.get(field.pos());
    
    // If not cached, get the default Java value by encoding the default JSON
    // value and then decoding it:
    if (defaultValue == null) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      encoder = EncoderFactory.get().binaryEncoder(baos, encoder);
      ResolvingGrammarGenerator.encode(
          encoder, field.schema(), defaultJsonValue);
      encoder.flush();
      decoder = DecoderFactory.get().binaryDecoder(
          baos.toByteArray(), decoder);
      defaultValue = data.createDatumReader(
          field.schema()).read(null, decoder);
      defaultSchemaValues.putIfAbsent(field.pos(), defaultValue);
    }
    
    // Make a deep copy of the default value so that subsequent mutations 
    // will not affect the default value cache:
    return data.deepCopy(field.schema(), defaultValue);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(fieldSetFlags);
    result = prime * result + ((schema == null) ? 0 : schema.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    RecordBuilderBase other = (RecordBuilderBase) obj;
    if (!Arrays.equals(fieldSetFlags, other.fieldSetFlags))
      return false;
    if (schema == null) {
      if (other.schema != null)
        return false;
    } else if (!schema.equals(other.schema))
      return false;
    return true;
  }
}
