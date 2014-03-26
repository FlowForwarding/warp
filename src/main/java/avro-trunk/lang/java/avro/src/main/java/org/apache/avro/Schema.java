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
 * 
 * Portions Copyright (c) 2013 FlowForwarding.Org
 * All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.avro;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.EnumSet;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.IntNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.DoubleNode;

/** An abstract data type.
 * <p>A schema may be one of:
 * <ul>
 * <li>A <i>record</i>, mapping field names to field value data;
 * <li>An <i>enum</i>, containing one of a small set of symbols;
 * <li>An <i>array</i> of values, all of the same schema;
 * <li>A <i>map</i>, containing string/value pairs, of a declared schema;
 * <li>A <i>union</i> of other schemas;
 * <li>A <i>fixed</i> sized binary object;
 * <li>A unicode <i>string</i>;
 * <li>A sequence of <i>bytes</i>;
 * <li>A 32-bit signed <i>int</i>;
 * <li>A 64-bit signed <i>long</i>;
 * <li>A 32-bit IEEE single-<i>float</i>; or
 * <li>A 64-bit IEEE <i>double</i>-float; or
 * <li>A <i>boolean</i>; or
 * <li><i>null</i>.
 * </ul>
 * 
 * A schema can be constructed using one of its static <tt>createXXX</tt>
 * methods. The schema objects are <i>logically</i> immutable.
 * There are only two mutating methods - {@link #setFields(List)} and
 * {@link #addProp(String, String)}. The following restrictions apply on these
 * two methods.
 * <ul>
 * <li> {@link #setFields(List)}, can be called at most once. This method exists
 * in order to enable clients to build recursive schemas.
 * <li> {@link #addProp(String, String)} can be called with property names
 * that are not present already. It is not possible to change or delete an
 * existing property.
 * </ul>
 */
public abstract class Schema extends JsonProperties {
  static final JsonFactory FACTORY = new JsonFactory();
  static final ObjectMapper MAPPER = new ObjectMapper(FACTORY);

  private static final int NO_HASHCODE = Integer.MIN_VALUE;

  static {
    FACTORY.enable(JsonParser.Feature.ALLOW_COMMENTS);
    FACTORY.setCodec(MAPPER);
  }

  /** The type of a schema. */
  public enum Type {
    RECORD, ENUM, ARRAY, MAP, UNION, FIXED, STRING, BYTES,
      INT, LONG, FLOAT, DOUBLE, BOOLEAN, BITMAP, NULL; //TODO OF Changes: Type.BITMAP
    private String name;
    private Type() { this.name = this.name().toLowerCase(); }
    public String getName() { return name; }
  };

  public enum BitOperations {
    AND, OR, XOR;    
    private String name;
    private BitOperations() { this.name = this.name().toLowerCase(); }
    public String getName() { return name; }
  };

  private final Type type;

  Schema(Type type) {
    super(SCHEMA_RESERVED);
    this.type = type;
  }

  /** Create a schema for a primitive type. */
  public static Schema create(Type type) {
    switch (type) {
    case STRING:  return new StringSchema();
    case BYTES:   return new BytesSchema();
    case INT:     return new IntSchema();
    case LONG:    return new LongSchema();
    case FLOAT:   return new FloatSchema();
    case DOUBLE:  return new DoubleSchema();
    case BOOLEAN: return new BooleanSchema();
    case NULL:    return new NullSchema();
    default: throw new AvroRuntimeException("Can't create a: "+type);
    }
  }

  private static final Set<String> SCHEMA_RESERVED = new HashSet<String>();
  static {
    Collections.addAll(SCHEMA_RESERVED,
                       "doc", "fields", "items", "name", "namespace",
                       "size", "symbols", "values", "type", "aliases");
  }

  int hashCode = NO_HASHCODE;

  @Override public void addProp(String name, JsonNode value) {
    super.addProp(name, value);
    hashCode = NO_HASHCODE;
  }

  /** Create an anonymous record schema. */
  public static Schema createRecord(List<Field> fields) {
    Schema result = createRecord(null, null, null, false);
    result.setFields(fields);
    return result;
  }

  /** Create a named record schema. */
  public static Schema createRecord(String name, String doc, String namespace,
                                    boolean isError) {
    return new RecordSchema(new Name(name, namespace), doc, isError);
  }

  /** Create an enum schema. */
  public static Schema createEnum(String name, String doc, String namespace,
                                  List<String> values) {
    return new EnumSchema(new Name(name, namespace), doc,
        new LockableArrayList<String>(values));
  }

  /** Create an array schema. */
  public static Schema createArray(Schema elementType) {
    return new ArraySchema(elementType);
  }

  /** Create a map schema. */
  public static Schema createMap(Schema valueType) {
    return new MapSchema(valueType);
  }

  /** Create a union schema. */
  public static Schema createUnion(List<Schema> types) {
    return new UnionSchema(new LockableArrayList<Schema>(types));
  }

  /** Create a union schema. */
  public static Schema createFixed(String name, String doc, String space,
      int size) {
    return new FixedSchema(new Name(name, space), doc, size);
  }

  /** Return the type of this schema. */
  public Type getType() { return type; }

  /**
   * If this is a record, returns the fields in it. The returned
   * list is in the order of their positions.
   */
  public org.apache.avro.Schema.BitmapSchema.IOperation getOperation() {
    throw new AvroRuntimeException("Not a bitmap: "+this);
  }

  /**
   * If this is a record, returns the Field with the
   * given name <tt>fieldName</tt>. If there is no field by that name, a
   * <tt>null</tt> is returned.
   */
  public Field getField(String fieldname) {
    throw new AvroRuntimeException("Not a record: "+this);
  }

  /**
   * If this is a record, returns the fields in it. The returned
   * list is in the order of their positions.
   */
  public List<Field> getFields() {
    throw new AvroRuntimeException("Not a record: "+this);
  }

  /**
   * If this is a record, set its fields. The fields can be set
   * only once in a schema.
   */
  public void setFields(List<Field> fields) {
    throw new AvroRuntimeException("Not a record: "+this);
  }

  /** If this is an enum, return its symbols. */
  public List<String> getEnumSymbols() {
    throw new AvroRuntimeException("Not an enum: "+this);
  }    

  /** If this is an enum, return a symbol's ordinal value. */
  public int getEnumOrdinal(String symbol) {
    throw new AvroRuntimeException("Not an enum: "+this);
  }    
  
  /** If this is an enum, returns true if it contains given symbol. */
  public boolean hasEnumSymbol(String symbol) {
    throw new AvroRuntimeException("Not an enum: "+this);
  }
  
  public JsonNode getEnumItem(String symbol) {
    throw new AvroRuntimeException("Not an enum: "+this);
  }
  
  public Schema getEnumItemsSchema() {
     throw new AvroRuntimeException("Not an enum: "+this);
   }
  
  /** If this is a record, enum or fixed, returns its name, otherwise the name
   * of the primitive type. */
  public String getName() { return type.name; }

  /** If this is a record, enum, or fixed, returns its docstring,
   * if available.  Otherwise, returns null. */
  public String getDoc() {
    return null;
  }

  /** If this is a record, enum or fixed, returns its namespace, if any. */
  public String getNamespace() {
    throw new AvroRuntimeException("Not a named type: "+this);
  }

  /** If this is a record, enum or fixed, returns its namespace-qualified name,
   * otherwise returns the name of the primitive type. */
  public String getFullName() {
    return getName();
  }

  /** If this is a record, enum or fixed, add an alias. */
  public void addAlias(String alias) {
    throw new AvroRuntimeException("Not a named type: "+this);
  }

  /** If this is a record, enum or fixed, return its aliases, if any. */
  public Set<String> getAliases() {
    throw new AvroRuntimeException("Not a named type: "+this);
  }

  /** Returns true if this record is an error type. */
  public boolean isError() {
    throw new AvroRuntimeException("Not a record: "+this);
  }

  /** If this is an array, returns its element type. */
  public Schema getElementType() {
    throw new AvroRuntimeException("Not an array: "+this);
  }

  /** If this is a map, returns its value type. */
  public Schema getValueType() {
    throw new AvroRuntimeException("Not a map: "+this);
  }

  /** If this is a union, returns its types. */
  public List<Schema> getTypes() {
    throw new AvroRuntimeException("Not a union: "+this);
  }

  /** If this is a union, return the branch with the provided full name. */
  public Integer getIndexNamed(String name) {
    throw new AvroRuntimeException("Not a union: "+this);
  }

  /** If this is fixed, returns its size. */
  public int getFixedSize() {
    throw new AvroRuntimeException("Not fixed: "+this);
  }

  /** Render this as <a href="http://json.org/">JSON</a>.*/
  @Override
  public String toString() { return toString(false); }

  /** Render this as <a href="http://json.org/">JSON</a>.
   * @param pretty if true, pretty-print JSON.
   */
  public String toString(boolean pretty) {
    try {
      StringWriter writer = new StringWriter();
      JsonGenerator gen = FACTORY.createJsonGenerator(writer);
      if (pretty) gen.useDefaultPrettyPrinter();
      toJson(new Names(), gen);
      gen.flush();
      return writer.toString();
    } catch (IOException e) {
      throw new AvroRuntimeException(e);
    }
  }

  void toJson(Names names, JsonGenerator gen) throws IOException {
    if (props.size() == 0) {                      // no props defined
      gen.writeString(getName());                 // just write name
    } else {
      gen.writeStartObject();
      gen.writeStringField("type", getName());
      writeProps(gen);
      gen.writeEndObject();
    }
  }

  void fieldsToJson(Names names, JsonGenerator gen) throws IOException {
    throw new AvroRuntimeException("Not a record: "+this);
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Schema)) return false;
    Schema that = (Schema)o;
    if (!(this.type == that.type)) return false;
    return equalCachedHash(that) && props.equals(that.props);
  }
  public final int hashCode() {
    if (hashCode == NO_HASHCODE)
      hashCode = computeHash();
    return hashCode;
  }

  int computeHash() { return getType().hashCode() + props.hashCode(); }

  final boolean equalCachedHash(Schema other) {
    return (hashCode == other.hashCode)
           || (hashCode == NO_HASHCODE)
           || (other.hashCode == NO_HASHCODE);
  }

  private static final Set<String> FIELD_RESERVED = new HashSet<String>();
  static {
    Collections.addAll(FIELD_RESERVED,
                       "default","doc","name","order","type","aliases");
  }

  // TODO OF Changes - New Enum <
  /** An item of Enum */
  /*public static class EnumItem extends JsonProperties {
    
    private final String name;
    private final String itemsType;
    private final Schema schema;
    private final JsonNode defaultValue;
    
    public EnumItem(String name, Schema schema, String type,
        JsonNode defaultValue) {
      super(FIELD_RESERVED);
      this.name = name;
      this.schema = schema;
      this.itemsType = type;
      this.defaultValue = defaultValue;
    }
    
    public String name() {
      return name;
    }

    public Schema schema() {
      return schema;
    }

    public JsonNode defaultValue() {
      return defaultValue;
    }

    
  }*/
  
  // TODO OF Changes - New Enum >    
  
  /** A field within a record. */
  public static class Field extends JsonProperties {

    /** How values of this field should be ordered when sorting records. */
    public enum Order {
      ASCENDING, DESCENDING, IGNORE;
      private String name;
      private Order() { this.name = this.name().toLowerCase(); }
    };

    private final String name;    // name of the field.
    private transient int position = -1;
    private final Schema schema;
    private final String doc;
    private final JsonNode defaultValue;
    private final Order order;
    private Set<String> aliases;

    public Field(String name, Schema schema, String doc,
        JsonNode defaultValue) {
      this(name, schema, doc, defaultValue, Order.ASCENDING);
    }
    public Field(String name, Schema schema, String doc,
        JsonNode defaultValue, Order order) {
      super(FIELD_RESERVED);
      this.name = validateName(name);
      this.schema = schema;
      this.doc = doc;
      this.defaultValue = defaultValue;
      this.order = order;
    }
    public String name() { return name; };
    /** The position of this field within the record. */
    public int pos() { return position; }
    /** This field's {@link Schema}. */
    public Schema schema() { return schema; }
    /** Field's documentation within the record, if set. May return null. */
    public String doc() { return doc; }
    public JsonNode defaultValue() { return defaultValue; }
    public Order order() { return order; }
    @Deprecated public Map<String,String> props() { return getProps(); }
    public void addAlias(String alias) {
      if (aliases == null)
        this.aliases = new LinkedHashSet<String>();
      aliases.add(alias);
    }
    /** Return the defined aliases as an unmodifieable Set. */
    public Set<String> aliases() {
      if (aliases == null)
        return Collections.emptySet();
      return Collections.unmodifiableSet(aliases);
    }
    public boolean equals(Object other) {
      if (other == this) return true;
      if (!(other instanceof Field)) return false;
      Field that = (Field) other;
      return (name.equals(that.name)) &&
        (schema.equals(that.schema)) &&
        defaultValueEquals(that.defaultValue) &&
        (order == that.order) &&
        props.equals(that.props);
    }
    public int hashCode() { return name.hashCode() + schema.computeHash(); }
    
    private boolean defaultValueEquals(JsonNode thatDefaultValue) {
      if (defaultValue == null)
        return thatDefaultValue == null;
      if (Double.isNaN(defaultValue.getDoubleValue()))
        return Double.isNaN(thatDefaultValue.getDoubleValue());
      return defaultValue.equals(thatDefaultValue);
    }

    @Override
    public String toString() {
      return name + " type:" + schema.type + " pos:" + position;
    }
  }

  private static class Name {
    private final String name;
    private final String space;
    private final String full;
    public Name(String name, String space) {
      if (name == null) {                         // anonymous
        this.name = this.space = this.full = null;
        return;
      }
      int lastDot = name.lastIndexOf('.');
      if (lastDot < 0) {                          // unqualified name
        this.space = space;                       // use default space
        this.name = validateName(name);
      } else {                                    // qualified name
        this.space = name.substring(0, lastDot);  // get space from name
        this.name = validateName(name.substring(lastDot+1, name.length()));
      }
      this.full = (this.space == null) ? this.name : this.space+"."+this.name;
    }
    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Name)) return false;
      Name that = (Name)o;
      return full==null ? that.full==null : full.equals(that.full);
    }
    public int hashCode() {
      return full==null ? 0 : full.hashCode();
    }
    public String toString() { return full; }
    public void writeName(Names names, JsonGenerator gen) throws IOException {
      if (name != null) gen.writeStringField("name", name);
      if (space != null) {
        if (!space.equals(names.space()))
          gen.writeStringField("namespace", space);
        if (names.space() == null)                // default namespace
          names.space(space);
      }
    }
    public String getQualified(String defaultSpace) {
      return (space == null || space.equals(defaultSpace)) ? name : full;
    }
  }

  private static abstract class NamedSchema extends Schema {
    final Name name;
    final String doc;
    Set<Name> aliases;
    public NamedSchema(Type type, Name name, String doc) {
      super(type);
      this.name = name;
      this.doc = doc;
      if (PRIMITIVES.containsKey(name.full)) {
        throw new AvroTypeException("Schemas may not be named after primitives: " + name.full);
      }
    }
    public String getName() { return name.name; }
    public String getDoc() { return doc; }
    public String getNamespace() { return name.space; }
    public String getFullName() { return name.full; }
    public void addAlias(String alias) {
      if (aliases == null)
        this.aliases = new LinkedHashSet<Name>();
      aliases.add(new Name(alias, name.space));
    }
    public Set<String> getAliases() {
      Set<String> result = new LinkedHashSet<String>();
      if (aliases != null)
        for (Name alias : aliases)
          result.add(alias.full);
      return result;
    }
    public boolean writeNameRef(Names names, JsonGenerator gen)
      throws IOException {
      if (this.equals(names.get(name))) {
        gen.writeString(name.getQualified(names.space()));
        return true;
      } else if (name.name != null) {
        names.put(name, this);
      }
      return false;
    }
    public void writeName(Names names, JsonGenerator gen) throws IOException {
      name.writeName(names, gen);
    }
    public boolean equalNames(NamedSchema that) {
      return this.name.equals(that.name);
    }
    @Override int computeHash() {
      return super.computeHash() + name.hashCode();
    }
    public void aliasesToJson(JsonGenerator gen) throws IOException {
      if (aliases == null || aliases.size() == 0) return;
      gen.writeFieldName("aliases");
      gen.writeStartArray();
      for (Name alias : aliases)
        gen.writeString(alias.getQualified(name.space));
      gen.writeEndArray();
    }

  }

  private static class SeenPair {
    private Object s1; private Object s2;
    private SeenPair(Object s1, Object s2) { this.s1 = s1; this.s2 = s2; }
    public boolean equals(Object o) {
      return this.s1 == ((SeenPair)o).s1 && this.s2 == ((SeenPair)o).s2;
    }
    public int hashCode() {
      return System.identityHashCode(s1) + System.identityHashCode(s2);
    }
  }

  private static final ThreadLocal<Set> SEEN_EQUALS = new ThreadLocal<Set>() {
    protected Set initialValue() { return new HashSet(); }
  };
  private static final ThreadLocal<Map> SEEN_HASHCODE = new ThreadLocal<Map>() {
    protected Map initialValue() { return new IdentityHashMap(); }
  };

  @SuppressWarnings(value="unchecked")
  private static class RecordSchema extends NamedSchema {
    private List<Field> fields;
    private Map<String, Field> fieldMap;
    private final boolean isError;
    public RecordSchema(Name name, String doc, boolean isError) {
      super(Type.RECORD, name, doc);
      this.isError = isError;
    }
    public boolean isError() { return isError; }

    @Override
    public Field getField(String fieldname) {
      if (fieldMap == null)
        throw new AvroRuntimeException("Schema fields not set yet");
      return fieldMap.get(fieldname);
    }

    @Override
    public List<Field> getFields() {
      if (fields == null)
        throw new AvroRuntimeException("Schema fields not set yet");
      return fields;
    }

    @Override
    public void setFields(List<Field> fields) {
      if (this.fields != null) {
        throw new AvroRuntimeException("Fields are already set");
      }
      int i = 0;
      fieldMap = new HashMap<String, Field>();
      LockableArrayList ff = new LockableArrayList();
      for (Field f : fields) {
        if (f.position != -1)
          throw new AvroRuntimeException("Field already used: " + f);
        f.position = i++;
        fieldMap.put(f.name(), f);
        ff.add(f);
      }
      this.fields = ff.lock();
      this.hashCode = NO_HASHCODE;
    }
    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof RecordSchema)) return false;
      RecordSchema that = (RecordSchema)o;
      if (!equalCachedHash(that)) return false;
      if (!equalNames(that)) return false;
      if (!props.equals(that.props)) return false;
      Set seen = SEEN_EQUALS.get();
      SeenPair here = new SeenPair(this, o);
      if (seen.contains(here)) return true;       // prevent stack overflow
      boolean first = seen.isEmpty();
      try {
        seen.add(here);
        return fields.equals(((RecordSchema)o).fields);
      } finally {
        if (first) seen.clear();
      }
    }
    @Override int computeHash() {
      Map seen = SEEN_HASHCODE.get();
      if (seen.containsKey(this)) return 0;       // prevent stack overflow
      boolean first = seen.isEmpty();
      try {
        seen.put(this, this);
        return super.computeHash() + fields.hashCode();
      } finally {
        if (first) seen.clear();
      }
    }
    void toJson(Names names, JsonGenerator gen) throws IOException {
      if (writeNameRef(names, gen)) return;
      String savedSpace = names.space;            // save namespace
      gen.writeStartObject();
      gen.writeStringField("type", isError?"error":"record");
      writeName(names, gen);
      names.space = name.space;                   // set default namespace
      if (getDoc() != null)
        gen.writeStringField("doc", getDoc());
      gen.writeFieldName("fields");
      fieldsToJson(names, gen);
      writeProps(gen);
      aliasesToJson(gen);
      gen.writeEndObject();
      names.space = savedSpace;                   // restore namespace
    }

    void fieldsToJson(Names names, JsonGenerator gen) throws IOException {
      gen.writeStartArray();
      for (Field f : fields) {
        gen.writeStartObject();
        gen.writeStringField("name", f.name());
        gen.writeFieldName("type");
        f.schema().toJson(names, gen);
        if (f.doc() != null)
          gen.writeStringField("doc", f.doc());
        if (f.defaultValue() != null) {
          gen.writeFieldName("default");
          gen.writeTree(f.defaultValue());
        }
        if (f.order() != Field.Order.ASCENDING)
          gen.writeStringField("order", f.order().name);
        if (f.aliases != null && f.aliases.size() != 0) {
          gen.writeFieldName("aliases");
          gen.writeStartArray();
          for (String alias : f.aliases)
            gen.writeString(alias);
          gen.writeEndArray();
        }
        f.writeProps(gen);
        gen.writeEndObject();
      }
      gen.writeEndArray();
    }
  }

  private static class EnumSchema extends NamedSchema {
    private final List<String> symbols;
    private final Map<String,Integer> ordinals;
    private final Map<String, JsonNode> items;
    private final Schema itemsSchema;    
     
    public EnumSchema(Name name, String doc,
        LockableArrayList<String> symbols) {
      super(Type.ENUM, name, doc);
      this.symbols = symbols.lock();
      this.ordinals = new HashMap<String,Integer>();
      this.items = null;
      this.itemsSchema = null;
      
      int i = 0;
      for (String symbol : symbols)
        if (ordinals.put(validateName(symbol), i++) != null)
          throw new SchemaParseException("Duplicate enum symbol: "+symbol);
    }

    public EnumSchema(Name name, String doc, Schema itemsSchema,
        LockableArrayList<JsonNode> list) {
      super(Type.ENUM, name, doc);
      //this.symbols = symbols.lock();
      this.ordinals = null;
      this.itemsSchema = itemsSchema;
      
      this.symbols = new ArrayList<String>();
      this.items = new HashMap<String, JsonNode>();
      
      for (JsonNode n : list) {
        JsonNode defaultValue = n.get("default");
        
        if (defaultValue.isObject()) {
          IOperation operation = new Operation(defaultValue);
          defaultValue = itemsSchema.getDefault(operation.result());
        }
        
        items.put(n.get("name").getTextValue(), defaultValue);  
        this.symbols.add(n.get("name").getTextValue());
      }

      return;
    }

    public List<String> getEnumSymbols() { return symbols; }
    public boolean hasEnumSymbol(String symbol) { 
      return ordinals.containsKey(symbol); }
    public int getEnumOrdinal(String symbol) { return ordinals.get(symbol); }
    public JsonNode getEnumItem(String symbol) { return items.get(symbol); }
    public Schema getEnumItemsSchema() { return this.itemsSchema; }
    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof EnumSchema)) return false;
      EnumSchema that = (EnumSchema)o;
      return equalCachedHash(that)
        && equalNames(that)
        && symbols.equals(that.symbols)
        && props.equals(that.props);
    }
    @Override int computeHash() { return super.computeHash() + symbols.hashCode(); }
    void toJson(Names names, JsonGenerator gen) throws IOException {
      if (writeNameRef(names, gen)) return;
      gen.writeStartObject();
      gen.writeStringField("type", "enum");
      writeName(names, gen);
      if (getDoc() != null)
        gen.writeStringField("doc", getDoc());
      gen.writeArrayFieldStart("symbols");
      for (String symbol : symbols)
        gen.writeString(symbol);
      gen.writeEndArray();
      writeProps(gen);
      aliasesToJson(gen);
      gen.writeEndObject();
    }
  }

  private static class ArraySchema extends Schema {
    private final Schema elementType;
    public ArraySchema(Schema elementType) {
      super(Type.ARRAY);
      this.elementType = elementType;
    }
    public Schema getElementType() { return elementType; }
    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof ArraySchema)) return false;
      ArraySchema that = (ArraySchema)o;
      return equalCachedHash(that)
        && elementType.equals(that.elementType)
        && props.equals(that.props);
    }
    @Override int computeHash() {
      return super.computeHash() + elementType.computeHash();
    }
    void toJson(Names names, JsonGenerator gen) throws IOException {
      gen.writeStartObject();
      gen.writeStringField("type", "array");
      gen.writeFieldName("items");
      elementType.toJson(names, gen);
      writeProps(gen);
      gen.writeEndObject();
    }
  }

  private static class MapSchema extends Schema {
    private final Schema valueType;
    public MapSchema(Schema valueType) {
      super(Type.MAP);
      this.valueType = valueType;
    }
    public Schema getValueType() { return valueType; }
    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof MapSchema)) return false;
      MapSchema that = (MapSchema)o;
      return equalCachedHash(that)
        && valueType.equals(that.valueType)
        && props.equals(that.props);
    }
    @Override int computeHash() {
      return super.computeHash() + valueType.computeHash();
    }
    void toJson(Names names, JsonGenerator gen) throws IOException {
      gen.writeStartObject();
      gen.writeStringField("type", "map");
      gen.writeFieldName("values");
      valueType.toJson(names, gen);
      writeProps(gen);
      gen.writeEndObject();
    }
  }

  private static class UnionSchema extends Schema {
    private final List<Schema> types;
    private final Map<String,Integer> indexByName
      = new HashMap<String,Integer>();
    public UnionSchema(LockableArrayList<Schema> types) {
      super(Type.UNION);
      this.types = types.lock();
      int index = 0;
      for (Schema type : types) {
        if (type.getType() == Type.UNION)
          throw new AvroRuntimeException("Nested union: "+this);
        String name = type.getFullName();
        if (name == null)
          throw new AvroRuntimeException("Nameless in union:"+this);
        if (indexByName.put(name, index++) != null)
          throw new AvroRuntimeException("Duplicate in union:" + name);
      }
    }
    public List<Schema> getTypes() { return types; }
    public Integer getIndexNamed(String name) { return indexByName.get(name); }
    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof UnionSchema)) return false;
      UnionSchema that = (UnionSchema)o;
      return equalCachedHash(that)
        && types.equals(that.types)
        && props.equals(that.props);
    }
    @Override int computeHash() {
      int hash = super.computeHash();
      for (Schema type : types)
        hash += type.computeHash();
      return hash;
    }
    
    @Override
    public void addProp(String name, String value) {
      throw new AvroRuntimeException("Can't set properties on a union: "+this);
    }
    
    void toJson(Names names, JsonGenerator gen) throws IOException {
      gen.writeStartArray();
      for (Schema type : types)
        type.toJson(names, gen);
      gen.writeEndArray();
    }
  }

  private static class FixedSchema extends NamedSchema {
    private final int size;
    public FixedSchema(Name name, String doc, int size) {
      super(Type.FIXED, name, doc);
      if (size < 0)
        throw new IllegalArgumentException("Invalid fixed size: "+size);
      this.size = size;
    }
    public int getFixedSize() { return size; }
    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof FixedSchema)) return false;
      FixedSchema that = (FixedSchema)o;
      return equalCachedHash(that)
        && equalNames(that)
        && size == that.size
        && props.equals(that.props);
    }
    @Override int computeHash() { return super.computeHash() + size; }
    void toJson(Names names, JsonGenerator gen) throws IOException {
      if (writeNameRef(names, gen)) return;
      gen.writeStartObject();
      gen.writeStringField("type", "fixed");
      writeName(names, gen);
      if (getDoc() != null)
        gen.writeStringField("doc", getDoc());
      gen.writeNumberField("size", size);
      writeProps(gen);
      aliasesToJson(gen);
      gen.writeEndObject();
    }
    
    @Override    
    public JsonNode getDefault (JsonNode n) {
      if (n.isInt()) {
        int value = n.getIntValue();

        List<Byte> temp = new ArrayList<Byte> (this.size);
        ArrayNode nn = JsonNodeFactory.instance.arrayNode();
        for (int i=0; i<this.size; i++) {
          nn.add(value & 255);
          value = value >> 8;
        }
        
        return nn;
        
      } else return null;
    }
  }

/*
 * OPERATIONS
 */
  public interface IOperation {
    JsonNode result();
  };
  
  protected interface IOperationBuilder {
     Operation build(JsonNode ops);
  };
  
  public class Operation implements IOperation {
    protected String name;
    protected List<IOperation> operands = null;
    protected JsonNode result = null;
    
    protected Map<String, IOperationBuilder> operations = null;
    
    protected class OperationOrBuilder implements IOperationBuilder{
       public Operation build(JsonNode ops) {
          return new OperationOr (ops);
       }
     }
    
    protected class OperationXorBuilder implements IOperationBuilder{
       public Operation build(JsonNode ops) {
          return new OperationXor (ops);
       }
    }
    
    protected class OperationAndBuilder implements IOperationBuilder{
       public Operation build(JsonNode ops) {
          return new OperationAnd (ops);
       }
    }
    
    protected class OperationShiftBuilder implements IOperationBuilder{
       public Operation build(JsonNode ops) {
          return new OperationShift(ops);
       }
    }
    
    protected class OperationBitsBuilder implements IOperationBuilder{
       public Operation build(JsonNode ops) {
          return new OperationBits(ops);
       }
    }
    
    protected void init () {
      if (operations == null) {
        operations = new HashMap<String, IOperationBuilder>();
        operations.put("or", new OperationOrBuilder());
        operations.put("xor", new OperationXorBuilder());
        operations.put("and", new OperationAndBuilder());
        operations.put("shift", new OperationShiftBuilder());
        operations.put("set_bits", new OperationBitsBuilder());
      }
   }
    /*
          String opName = operand.getFieldNames().next();
          if (opName.equalsIgnoreCase("or")) 
            this.operands.add(new OperationOr(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("and"))
            this.operands.add(new OperationAnd(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("xor"))
            this.operands.add(new OperationXor(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("shift"))
            this.operands.add(new OperationShift(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("set_bits"))
            this.operands.add(new OperationBits(operand.getElements().next()));


    */
    
    
    public Operation (String n, List<IOperation> ops) {
      init();
      this.name = n;
      this.operands = ops;
    }
    
    public Operation (JsonNode n) {
      init();

      if (n.isInt()) {
        this.name = "nop";
        this.result = n;
      } else {
        this.operands = new ArrayList<IOperation>();
        this.name = n.getFieldNames().next();
        
        JsonNode ops = n.getElements().next();
        JsonNode operand = null;

        Iterator <JsonNode> it = ops.getElements();
        while (it.hasNext()) {
          operand = it.next();
          this.operands.add(new Operation(operand));
        }        
      }
    }

    public Operation () {
      init();
      this.name = "nop";
      this.operands = new ArrayList<IOperation> ();
    }
    
    @Override
    public JsonNode result() {
      if (this.name.equalsIgnoreCase("nop")) {
        return result;
      } else {
        if (this.name.equalsIgnoreCase("and")) {
          return resultAnd();
        } else if (this.name.equalsIgnoreCase("or")) {
          return resultOr();
        } else if (this.name.equalsIgnoreCase("xor")) {
          return resultXor();
        } else if (this.name.equalsIgnoreCase("shift")) {
          return resultShift();
        } else return null;
      }
    }
    
    private JsonNode resultOr () {
      int result = 0;

      for ( IOperation operand: operands) {
        result |= operand.result().getValueAsInt();
      }

      return new IntNode (result);
    }
    
    private JsonNode resultAnd() {
      int result = 255;

      for ( IOperation operand: operands) {
        result &= operand.result().getValueAsInt();
      }

      return new IntNode (result);
    }
    
    private JsonNode resultXor() {
      int result = 0;

      for ( IOperation operand: operands) {
        result ^= operand.result().getValueAsInt();
      }

      return new IntNode (result);
    }
    
    private JsonNode resultShift() {
      int result = operands.get(0).result().getValueAsInt();
      int factor = operands.get(1).result().getValueAsInt();

      return new IntNode (result << factor);
    }
    
  };
  

  public class OperationValue extends Operation {
    IntNode value;      

    public OperationValue(IntNode val) {
      super();
      this.value = val;
    }

    @Override
    public JsonNode result() {
      return this.value;
    }
  }

  public class OperationOr extends Operation {
  
    public OperationOr (List<IOperation> ops) {
      super ("or", ops);
    }

    public OperationOr (JsonNode ops) {
      super ();
      name = "or";
      JsonNode operand = null;

      Iterator <JsonNode> it = ops.getElements();
      while (it.hasNext()) {
        operand = it.next();
        if (operand.isInt() ) {
          this.operands.add(new OperationValue((IntNode) operand));
        } else {
          String opName = operand.getFieldNames().next();
          operands.add(operations.get(opName).build(operand.getElements().next()));
/*          if (opName.equalsIgnoreCase("or")) 
            this.operands.add(new OperationOr(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("and"))
            this.operands.add(new OperationAnd(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("xor"))
            this.operands.add(new OperationXor(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("shift"))
            this.operands.add(new OperationShift(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("set_bits"))
            this.operands.add(new OperationBits(operand.getElements().next()));*/
          
        }
      }
    }
    
    @Override
    public JsonNode result() {
      int result = 0;

      for ( IOperation operand: operands) {
        result |= operand.result().getValueAsInt();
      }

      return new IntNode (result);
    }
  
  }

  public class OperationAnd extends Operation {
  
    public OperationAnd (List<IOperation> ops) {
      super ("and", ops);
    }

    public OperationAnd (JsonNode ops) {
      super ();
      name = "and";
      JsonNode operand = null;

      Iterator <JsonNode> it = ops.getElements();
      while (it.hasNext()) {
        operand = it.next();
        if (operand.isInt() ) {
          this.operands.add(new OperationValue((IntNode) operand));
        } else {
          String opName = operand.getFieldNames().next();
          operands.add(operations.get(opName).build(operand.getElements().next()));
/*          if (opName.equalsIgnoreCase("or")) 
            this.operands.add(new OperationOr(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("and"))
            this.operands.add(new OperationAnd(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("xor"))
            this.operands.add(new OperationXor(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("shift"))
            this.operands.add(new OperationShift(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("set_bits"))
            this.operands.add(new OperationBits(operand.getElements().next()));*/
        }
      }
    }
    
    @Override
    public JsonNode result() {
      int result = 255;

      for ( IOperation operand: operands) {
        result &= operand.result().getValueAsInt();
      }

      return new IntNode (result);
    }
  
  }

  public class OperationXor extends Operation {
  
    public OperationXor (List<IOperation> ops) {
      super ("xor", ops);
    }

    public OperationXor (JsonNode ops) {
      super ();
      name = "xor";
      JsonNode operand = null;

      Iterator <JsonNode> it = ops.getElements();
      while (it.hasNext()) {
        operand = it.next();
        if (operand.isInt() ) {
          this.operands.add(new OperationValue((IntNode) operand));
        } else {
          String opName = operand.getFieldNames().next();
          operands.add(operations.get(opName).build(operand.getElements().next()));
/*          if (opName.equalsIgnoreCase("or")) 
            this.operands.add(new OperationOr(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("and"))
            this.operands.add(new OperationAnd(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("xor"))
            this.operands.add(new OperationXor(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("shift"))
            this.operands.add(new OperationShift(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("set_bits"))
            this.operands.add(new OperationBits(operand.getElements().next()));*/
        }
      }
    }
    
    @Override
    public JsonNode result() {
      int result = 0;

      for ( IOperation operand: operands) {
        result ^= operand.result().getValueAsInt();
      }

      return new IntNode (result);
    }
  
  }

  public class OperationShift extends Operation {
  
    public OperationShift (List<IOperation> ops) {
      super ("shift", ops);
    }

    public OperationShift (JsonNode ops) {
      super ();
      name = "shift";
      JsonNode operand = null;

      Iterator <JsonNode> it = ops.getElements();
      while (it.hasNext()) {
        operand = it.next();
        if (operand.isInt() ) {
          this.operands.add(new OperationValue((IntNode) operand));
        } else {
          String opName = operand.getFieldNames().next();
          operands.add(operations.get(opName).build(operand.getElements().next()));
/*          if (opName.equalsIgnoreCase("or")) 
            this.operands.add(new OperationOr(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("and"))
            this.operands.add(new OperationAnd(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("xor"))
            this.operands.add(new OperationXor(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("shift"))
            this.operands.add(new OperationShift(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("set_bits"))
            this.operands.add(new OperationBits(operand.getElements().next()));*/
        }
      }
    }
    
    @Override
    public JsonNode result() {
      int result = operands.get(0).result().getValueAsInt();
      int factor = operands.get(1).result().getValueAsInt();

      return new IntNode (result << factor);
    }
  
  }

  public class OperationBits extends Operation {
  
    public OperationBits (List<IOperation> ops) {
      super ("set_bits", ops);
    }

    public OperationBits (JsonNode ops) {
      super ();
      name = "set_bits";
      JsonNode operand = null;

      Iterator <JsonNode> it = ops.getElements();
      while (it.hasNext()) {
        operand = it.next();
        if (operand.isInt() ) {
          this.operands.add(new OperationValue((IntNode) operand));
        } else {
          String opName = operand.getFieldNames().next();
          operands.add(operations.get(opName).build(operand.getElements().next()));
/*          if (opName.equalsIgnoreCase("or")) 
            this.operands.add(new OperationOr(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("and"))
            this.operands.add(new OperationAnd(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("xor"))
            this.operands.add(new OperationXor(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("shift"))
            this.operands.add(new OperationShift(operand.getElements().next()));
          else if (opName.equalsIgnoreCase("set_bits"))
            this.operands.add(new OperationBits(operand.getElements().next()));*/
        }
      }
    }
    
    @Override
    public JsonNode result() {
      int result = operands.get(0).result().getValueAsInt();

      return new IntNode (result);
    }
  
  }

  private static class BitmapSchema extends NamedSchema { //TODO OF Changes: BitmapSchema

    private final int size;
    boolean isError;
    private IOperation defaultValue = null;

    public IOperation getOperation() {
      if (defaultValue == null)
        throw new AvroRuntimeException("Schema operation not set yet");
      return defaultValue;
    }

    public BitmapSchema(Name name, JsonNode sizeNode, JsonNode defaultValue, String doc, int size, boolean isError) {
      super(Type.BITMAP, name, doc);
      this.isError = isError;
      this.size = size;
      
      if (defaultValue != null) {
        if (defaultValue.isInt()) {
          this.defaultValue = new OperationValue((IntNode)defaultValue);
        } else {

          String opName = defaultValue.getFieldNames().next();
          JsonNode operands = defaultValue.getElements().next();
        
          if (opName.equalsIgnoreCase("or"))
            this.defaultValue = new OperationOr(operands);
          else if (opName.equalsIgnoreCase("and"))
            this.defaultValue = new OperationAnd(operands);
          else if (opName.equalsIgnoreCase("xor"))
            this.defaultValue = new OperationXor(operands);
          else if (opName.equalsIgnoreCase("shift"))
            this.defaultValue = new OperationShift(operands);
          else if (opName.equalsIgnoreCase("set_bits"))
            this.defaultValue = new OperationBits(operands);
          else if (opName.equalsIgnoreCase("or"))
            this.defaultValue = new OperationOr(operands);
        }
        
        JsonNode result = this.defaultValue.result();
      }
    }      
             
/*
        if (operations.contains(BitOperations.AND)) {
          result = null;
        }
        
        Iterator<JsonNode> opIt = operands.getElements();

        while (opIt.hasNext()) {
          JsonNode operand = opIt.next();
          continue;
        }
      public BitmapSchema(Name name, String doc, int size, JsonNode defaultValue) {
      super(Type.BITMAP, name, doc);
      if (size < 0)
        throw new IllegalArgumentException("Invalid fixed size: "+size);
      this.size = size;
      this.value = defaultValue;
    }*/
  }

  private static class StringSchema extends Schema {
    public StringSchema() { super(Type.STRING); }
  }

  private static class BytesSchema extends Schema {
    public BytesSchema() { super(Type.BYTES); }
  }

  private static class IntSchema extends Schema {
    public IntSchema() { super(Type.INT); }
  }

  private static class LongSchema extends Schema {
    public LongSchema() { super(Type.LONG); }
  }

  private static class FloatSchema extends Schema {
    public FloatSchema() { super(Type.FLOAT); }
  }

  private static class DoubleSchema extends Schema {
    public DoubleSchema() { super(Type.DOUBLE); }
  }

  private static class BooleanSchema extends Schema {
    public BooleanSchema() { super(Type.BOOLEAN); }
  }
  
  private static class NullSchema extends Schema {
    public NullSchema() { super(Type.NULL); }
  }

  /** A parser for JSON-format schemas.  Each named schema parsed with a parser
   * is added to the names known to the parser so that subsequently parsed
   * schemas may refer to it by name. */
  public static class Parser {
    private Names names = new Names();
    private boolean validate = true;

    /** Adds the provided types to the set of defined, named types known to
     * this parser. */
    public Parser addTypes(Map<String,Schema> types) {
      for (Schema s : types.values())
        names.add(s);
      return this;
    }

    /** Returns the set of defined, named types known to this parser. */
    public Map<String,Schema> getTypes() {
      Map<String,Schema> result = new LinkedHashMap<String,Schema>();
      for (Schema s : names.values())
        result.put(s.getFullName(), s);
      return result;
    }

    /** Enable or disable name validation. */
    public Parser setValidate(boolean validate) {
      this.validate = validate;
      return this;
    }

    /** True iff names are validated.  True by default. */
    public boolean getValidate() { return this.validate; }

    /** Parse a schema from the provided file.
     * If named, the schema is added to the names known to this parser. */
    public Schema parse(File file) throws IOException {
      return parse(FACTORY.createJsonParser(file));
    }

    /** Parse a schema from the provided stream.
     * If named, the schema is added to the names known to this parser. */
    public Schema parse(InputStream in) throws IOException {
      return parse(FACTORY.createJsonParser(in));
    }

    /** Parse a schema from the provided string.
     * If named, the schema is added to the names known to this parser. */
    public Schema parse(String s) {
      try {
        return parse(FACTORY.createJsonParser(new StringReader(s)));
      } catch (IOException e) {
        throw new SchemaParseException(e);
      }
    }

    private Schema parse(JsonParser parser) throws IOException {
      boolean saved = validateNames.get();
      try {
        validateNames.set(validate);
        return Schema.parse(MAPPER.readTree(parser), names);
      } catch (JsonParseException e) {
        throw new SchemaParseException(e);
      } finally {
        validateNames.set(saved);
      }
    }
  }

  /**
   * Constructs a Schema object from JSON schema file <tt>file</tt>.
   * The contents of <tt>file</tt> is expected to be in UTF-8 format.
   * @param file  The file to read the schema from.
   * @return  The freshly built Schema.
   * @throws IOException if there was trouble reading the contents
   * @throws JsonParseException if the contents are invalid
   * @deprecated use {@link Schema.Parser} instead.
   */
  public static Schema parse(File file) throws IOException {
    return new Parser().parse(file);
  }

  /**
   * Constructs a Schema object from JSON schema stream <tt>in</tt>.
   * The contents of <tt>in</tt> is expected to be in UTF-8 format.
   * @param in  The input stream to read the schema from.
   * @return  The freshly built Schema.
   * @throws IOException if there was trouble reading the contents
   * @throws JsonParseException if the contents are invalid
   * @deprecated use {@link Schema.Parser} instead.
   */
  public static Schema parse(InputStream in) throws IOException {
    return new Parser().parse(in);
  }

  /** Construct a schema from <a href="http://json.org/">JSON</a> text.
   * @deprecated use {@link Schema.Parser} instead.
   */
  public static Schema parse(String jsonSchema) {
    return new Parser().parse(jsonSchema);
  }

  /** Construct a schema from <a href="http://json.org/">JSON</a> text.
   * @param validate true if names should be validated, false if not.
   * @deprecated use {@link Schema.Parser} instead.
   */
  public static Schema parse(String jsonSchema, boolean validate) {
    return new Parser().setValidate(validate).parse(jsonSchema);
  }

  static final Map<String,Type> PRIMITIVES = new HashMap<String,Type>();
  static {
    PRIMITIVES.put("string",  Type.STRING);
    PRIMITIVES.put("bytes",   Type.BYTES);
    PRIMITIVES.put("int",     Type.INT);
    PRIMITIVES.put("long",    Type.LONG);
    PRIMITIVES.put("float",   Type.FLOAT);
    PRIMITIVES.put("double",  Type.DOUBLE);
    PRIMITIVES.put("boolean", Type.BOOLEAN);
    PRIMITIVES.put("null",    Type.NULL);
  }

  static class Names extends LinkedHashMap<Name, Schema> {
    private String space;                         // default namespace

    public Names() {}
    public Names(String space) { this.space = space; }

    public String space() { return space; }
    public void space(String space) { this.space = space; }

    @Override
    public Schema get(Object o) {
      Name name;
      if (o instanceof String) {
        Type primitive = PRIMITIVES.get((String)o);
        if (primitive != null) return Schema.create(primitive);
        name = new Name((String)o, space);
      } else {
        name = (Name)o;
      }
      return super.get(name);
    }
    public boolean contains(Schema schema) {
      return get(((NamedSchema)schema).name) != null;
    }
    public void add(Schema schema) {
      put(((NamedSchema)schema).name, schema);
    }
    @Override
    public Schema put(Name name, Schema schema) {
      if (containsKey(name))
        throw new SchemaParseException("Can't redefine: "+name);
      return super.put(name, schema);
    }
  }
  
  private static ThreadLocal<Boolean> validateNames
    = new ThreadLocal<Boolean>() {
    @Override protected Boolean initialValue() {
      return true;
    }
  };
    
  private static String validateName(String name) {
    if (!validateNames.get()) return name;        // not validating names
    int length = name.length();
    if (length == 0)
      throw new SchemaParseException("Empty name");
    char first = name.charAt(0);
    if (!(Character.isLetter(first) || first == '_'))
      throw new SchemaParseException("Illegal initial character: "+name);
    for (int i = 1; i < length; i++) {
      char c = name.charAt(i);
      if (!(Character.isLetterOrDigit(c) || c == '_'))
        throw new SchemaParseException("Illegal character in: "+name);
    }
    return name;
  }

  /** @see #parse(String) */
  static Schema parse(JsonNode schema, Names names) {
    if (schema.isTextual()) {                     // name
      Schema result = names.get(schema.getTextValue());
      if (result == null)
        throw new SchemaParseException("Undefined name: "+schema);
      return result;
    } else if (schema.isObject()) {
      Schema result;
      String type = getRequiredText(schema, "type", "No type");
      Name name = null;
      String savedSpace = null;
      String doc = null;
      if (type.equals("record") || type.equals("error")
          || type.equals("enum") || type.equals("fixed") || type.equals("bitmap")) {
        String space = getOptionalText(schema, "namespace");
        doc = getOptionalText(schema, "doc");
        if (space == null)
          space = names.space();
        name = new Name(getRequiredText(schema, "name", "No name in schema"),
                        space);
        if (name.space != null) {                 // set default namespace
          savedSpace = names.space();
          names.space(name.space);
        }
      }
      if (PRIMITIVES.containsKey(type)) {         // primitive
        result = create(PRIMITIVES.get(type));
      } else if (type.equals("record") || type.equals("error")) { // record
        List<Field> fields = new ArrayList<Field>();
        result = new RecordSchema(name, doc, type.equals("error"));
        if (name != null) names.add(result);
        JsonNode fieldsNode = schema.get("fields");
        if (fieldsNode == null || !fieldsNode.isArray())
          throw new SchemaParseException("Record has no fields: "+schema);
        for (JsonNode field : fieldsNode) {
          String fieldName = getRequiredText(field, "name", "No field name");
          String fieldDoc = getOptionalText(field, "doc");
          JsonNode fieldTypeNode = field.get("type");
          if (fieldTypeNode == null)
            throw new SchemaParseException("No field type: "+field);
          if (fieldTypeNode.isTextual()
              && names.get(fieldTypeNode.getTextValue()) == null)
            throw new SchemaParseException
              (fieldTypeNode+" is not a defined name."
               +" The type of the \""+fieldName+"\" field must be"
               +" a defined name or a {\"type\": ...} expression.");
          Schema fieldSchema = parse(fieldTypeNode, names);
          Field.Order order = Field.Order.ASCENDING;
          JsonNode orderNode = field.get("order");
          if (orderNode != null)
            order = Field.Order.valueOf(orderNode.getTextValue().toUpperCase());
          JsonNode defaultValue = field.get("default");
          if (defaultValue != null
              && (Type.FLOAT.equals(fieldSchema.getType())
                  || Type.DOUBLE.equals(fieldSchema.getType()))
              && defaultValue.isTextual()) {
            defaultValue =
              new DoubleNode(Double.valueOf(defaultValue.getTextValue()));
          } else if ((defaultValue != null) && (defaultValue.isTextual())) {
            defaultValue = ((EnumSchema)fieldSchema).getEnumItem(defaultValue.getTextValue());
            fieldSchema = ((EnumSchema)fieldSchema).getEnumItemsSchema();
          }
          
          Field f = new Field(fieldName, fieldSchema,
                              fieldDoc, defaultValue, order);
          Iterator<String> i = field.getFieldNames();
          while (i.hasNext()) {                       // add field props
            String prop = i.next();
            if (!FIELD_RESERVED.contains(prop))
              f.addProp(prop, field.get(prop));
          }
          f.aliases = parseAliases(field);
          fields.add(f);
        }
        result.setFields(fields);
      } else if (type.equals("enum")) {           // enum
        JsonNode symbolsNode = schema.get("symbols"); // TODO OF Changes - New Enum <
        JsonNode itemsNode = schema.get("items");
        JsonNode listNode = schema.get("list");        
        result = null;
        if ((symbolsNode == null || !symbolsNode.isArray()) &&
           (itemsNode == null || listNode == null)) 
          throw new SchemaParseException("Enum has neither symbols nor items: "+schema);
        if (symbolsNode != null) {
          LockableArrayList<String> symbols = new LockableArrayList<String>();
          for (JsonNode n : symbolsNode)
            symbols.add(n.getTextValue());
          result = new EnumSchema(name, doc, symbols);
          if (name != null) names.add(result);
        }
        
        else if (itemsNode != null && listNode != null) {
          Schema itemsSchema = parse(itemsNode, names);//String itemsType = itemsNode.get;
          LockableArrayList<JsonNode> list = new LockableArrayList<JsonNode>();
          LockableArrayList<String> symbols = new LockableArrayList<String>();
          Iterator<JsonNode> i =  listNode.getElements();
          while (i.hasNext()) {
            JsonNode n = i.next();
            symbols.add(n.get("name").getTextValue());
            list.add(n);
          }
          result = new EnumSchema(name, doc, itemsSchema, list);
          if (name != null) names.add(result);
        }                                             // TODO OF Changes - New Enum >
      } else if (type.equals("array")) {          // array
        JsonNode itemsNode = schema.get("items");
        if (itemsNode == null)
          throw new SchemaParseException("Array has no items type: "+schema);
        result = new ArraySchema(parse(itemsNode, names));
      } else if (type.equals("map")) {            // map
        JsonNode valuesNode = schema.get("values");
        if (valuesNode == null)
          throw new SchemaParseException("Map has no values type: "+schema);
        result = new MapSchema(parse(valuesNode, names));
      } else if (type.equals("fixed")) {          // fixed
        JsonNode sizeNode = schema.get("size");
        if (sizeNode == null || !sizeNode.isInt())
          throw new SchemaParseException("Invalid or no size: "+schema);
        result = new FixedSchema(name, doc, sizeNode.getIntValue());
        if (name != null) names.add(result);
      } else if (type.equals("bitmap")) { // TODO OF changes - Bit operations
        EnumSet<BitOperations> operations = EnumSet.allOf(BitOperations.class);
        JsonNode sizeNode = schema.get("size");
        JsonNode defaultValue = schema.get("default");
        result = new BitmapSchema(name, sizeNode, defaultValue, doc, 0, type.equals("error"));
        if (name != null) names.add(result);
      } else
        throw new SchemaParseException("Type not supported: "+type);
      Iterator<String> i = schema.getFieldNames();
      while (i.hasNext()) {                       // add properties
        String prop = i.next();
        if (!SCHEMA_RESERVED.contains(prop))      // ignore reserved
          result.addProp(prop, schema.get(prop));
      }
      if (savedSpace != null)
        names.space(savedSpace);                  // restore space
      if (result instanceof NamedSchema) {
        Set<String> aliases = parseAliases(schema);
        if (aliases != null)                      // add aliases
          for (String alias : aliases)
            result.addAlias(alias);
      }
      return result;
    } else if (schema.isArray()) {                // union
      LockableArrayList<Schema> types =
        new LockableArrayList<Schema>(schema.size());
      for (JsonNode typeNode : schema)
        types.add(parse(typeNode, names));
      return new UnionSchema(types);
    } else {
      throw new SchemaParseException("Schema not yet supported: "+schema);
    }
  }

  private static Set<String> parseAliases(JsonNode node) {
    JsonNode aliasesNode = node.get("aliases");
    if (aliasesNode == null)
      return null;
    if (!aliasesNode.isArray())
      throw new SchemaParseException("aliases not an array: "+node);
    Set<String> aliases = new LinkedHashSet<String>();
    for (JsonNode aliasNode : aliasesNode) {
      if (!aliasNode.isTextual())
        throw new SchemaParseException("alias not a string: "+aliasNode);
      aliases.add(aliasNode.getTextValue());
    }
    return aliases;  
  }

  /** Extracts text value associated to key from the container JsonNode,
   * and throws {@link SchemaParseException} if it doesn't exist.
   *
   * @param container Container where to find key.
   * @param key Key to look for in container.
   * @param error String to prepend to the SchemaParseException.
   * @return
   */
  private static String getRequiredText(JsonNode container, String key,
      String error) {
    String out = getOptionalText(container, key);
    if (null == out) {
      throw new SchemaParseException(error + ": " + container);
    }
    return out;
  }

  /** Extracts text value associated to key from the container JsonNode. */
  private static String getOptionalText(JsonNode container, String key) {
    JsonNode jsonNode = container.get(key);
    return jsonNode != null ? jsonNode.getTextValue() : null;
  }

  static JsonNode parseJson(String s) {
    try {
      return MAPPER.readTree(FACTORY.createJsonParser(new StringReader(s)));
    } catch (JsonParseException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Rewrite a writer's schema using the aliases from a reader's schema.  This
   * permits reading records, enums and fixed schemas whose names have changed,
   * and records whose field names have changed.  The returned schema always
   * contains the same data elements in the same order, but with possibly
   * different names. */
  public static Schema applyAliases(Schema writer, Schema reader) {
    if (writer == reader) return writer;          // same schema

    // create indexes of names
    Map<Schema,Schema> seen = new IdentityHashMap<Schema,Schema>(1);
    Map<Name,Name> aliases = new HashMap<Name, Name>(1);
    Map<Name,Map<String,String>> fieldAliases =
      new HashMap<Name, Map<String,String>>(1);
    getAliases(reader, seen, aliases, fieldAliases);

    if (aliases.size() == 0 && fieldAliases.size() == 0)
      return writer;                              // no aliases
    
    seen.clear();
    return applyAliases(writer, seen, aliases, fieldAliases);
  }

  private static Schema applyAliases(Schema s, Map<Schema,Schema> seen,
                                     Map<Name,Name> aliases,
                                     Map<Name,Map<String,String>> fieldAliases){

    Name name = s instanceof NamedSchema ? ((NamedSchema)s).name : null;
    Schema result = s;
    switch (s.getType()) {
    case RECORD:
      if (seen.containsKey(s)) return seen.get(s); // break loops
      if (aliases.containsKey(name))
        name = aliases.get(name);
      result = Schema.createRecord(name.full, s.getDoc(), null, s.isError());
      seen.put(s, result);
      List<Field> newFields = new ArrayList<Field>();
      for (Field f : s.getFields()) {
        Schema fSchema = applyAliases(f.schema, seen, aliases, fieldAliases);
        String fName = getFieldAlias(name, f.name, fieldAliases);
        Field newF = new Field(fName, fSchema, f.doc, f.defaultValue, f.order);
        newF.props.putAll(f.props);               // copy props
        newFields.add(newF);
      }
      result.setFields(newFields);
      break;
    case ENUM:
      if (aliases.containsKey(name))
        result = Schema.createEnum(aliases.get(name).full, s.getDoc(), null,
                                   s.getEnumSymbols());
      break;
    case ARRAY:
      Schema e = applyAliases(s.getElementType(), seen, aliases, fieldAliases);
      if (e != s.getElementType())
        result = Schema.createArray(e);
      break;
    case MAP:
      Schema v = applyAliases(s.getValueType(), seen, aliases, fieldAliases);
      if (v != s.getValueType())
        result = Schema.createMap(v);
      break;
    case UNION:
      List<Schema> types = new ArrayList<Schema>();
      for (Schema branch : s.getTypes())
        types.add(applyAliases(branch, seen, aliases, fieldAliases));
      result = Schema.createUnion(types);
      break;
    case FIXED:
      if (aliases.containsKey(name))
        result = Schema.createFixed(aliases.get(name).full, s.getDoc(), null,
                                    s.getFixedSize());
      break;
    }
    if (result != s)
      result.props.putAll(s.props);        // copy props
    return result;
  }


  private static void getAliases(Schema schema,
                                 Map<Schema,Schema> seen,
                                 Map<Name,Name> aliases,
                                 Map<Name,Map<String,String>> fieldAliases) {
    if (schema instanceof NamedSchema) {
      NamedSchema namedSchema = (NamedSchema)schema;
      if (namedSchema.aliases != null)
        for (Name alias : namedSchema.aliases)
          aliases.put(alias, namedSchema.name);
    }
    switch (schema.getType()) {
    case RECORD:
      if (seen.containsKey(schema)) return;            // break loops
      seen.put(schema, schema);
      RecordSchema record = (RecordSchema)schema;
      for (Field field : schema.getFields()) {
        if (field.aliases != null)
          for (String fieldAlias : field.aliases) {
            Map<String,String> recordAliases = fieldAliases.get(record.name);
            if (recordAliases == null)
              fieldAliases.put(record.name,
                               recordAliases = new HashMap<String,String>());
            recordAliases.put(fieldAlias, field.name);
          }
        getAliases(field.schema, seen, aliases, fieldAliases);
      }
      if (record.aliases != null && fieldAliases.containsKey(record.name))
        for (Name recordAlias : record.aliases)
          fieldAliases.put(recordAlias, fieldAliases.get(record.name));
      break;
    case ARRAY:
      getAliases(schema.getElementType(), seen, aliases, fieldAliases);
      break;
    case MAP:
      getAliases(schema.getValueType(), seen, aliases, fieldAliases);
      break;
    case UNION:
      for (Schema s : schema.getTypes())
        getAliases(s, seen, aliases, fieldAliases);
      break;
    }
  }

  private static String getFieldAlias
    (Name record, String field, Map<Name,Map<String,String>> fieldAliases) {
    Map<String,String> recordAliases = fieldAliases.get(record);
    if (recordAliases == null)
      return field;
    String alias = recordAliases.get(field);
    if (alias == null)
      return field;
    return alias;
  }

  /**
   * No change is permitted on LockableArrayList once lock() has been
   * called on it.
   * @param <E>
   */
  
  /*
   * This class keeps a boolean variable <tt>locked</tt> which is set
   * to <tt>true</tt> in the lock() method. It's legal to call
   * lock() any number of times. Any lock() other than the first one
   * is a no-op.
   * 
   * This class throws <tt>IllegalStateException</tt> if a mutating
   * operation is performed after being locked. Since modifications through
   * iterator also use the list's mutating operations, this effectively
   * blocks all modifications.
   */
  static class LockableArrayList<E> extends ArrayList<E> {
    private static final long serialVersionUID = 1L;
    private boolean locked = false;
    
    public LockableArrayList() {
    }

    public LockableArrayList(int size) {
      super(size);
    }

    public LockableArrayList(List<E> types) {
      super(types);
    }

    public List<E> lock() {
      locked = true;
      return this;
    }

    private void ensureUnlocked() {
      if (locked) {
        throw new IllegalStateException();
      }
    }

    public boolean add(E e) {
      ensureUnlocked();
      return super.add(e);
    }
    
    public boolean remove(Object o) {
      ensureUnlocked();
      return super.remove(o);
    }
    
    public E remove(int index) {
      ensureUnlocked();
      return super.remove(index);
    }
      
    public boolean addAll(Collection<? extends E> c) {
      ensureUnlocked();
      return super.addAll(c);
    }
    
    public boolean addAll(int index, Collection<? extends E> c) {
      ensureUnlocked();
      return super.addAll(index, c);
    }
    
    public boolean removeAll(Collection<?> c) {
      ensureUnlocked();
      return super.removeAll(c);
    }
    
    public boolean retainAll(Collection<?> c) {
      ensureUnlocked();
      return super.retainAll(c);
    }
    
    public void clear() {
      ensureUnlocked();
      super.clear();
    }

  }
  
  public JsonNode getDefault (JsonNode n) {
    return null;
  }
}
