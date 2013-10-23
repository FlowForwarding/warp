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
package org.apache.avro.tool;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;

/** Reads new-line delimited JSON records and writers an Avro data file. */
public class DataFileWriteTool implements Tool {

  @Override
  public String getName() {
    return "fromjson";
  }

  @Override
  public String getShortDescription() {
    return "Reads JSON records and writes an Avro data file.";
  }

  @Override
  public int run(InputStream stdin, PrintStream out, PrintStream err,
      List<String> args) throws Exception {

    OptionParser p = new OptionParser();
    OptionSpec<String> codec =
      p.accepts("codec", "Compression codec")
      .withRequiredArg()
      .defaultsTo("null")
      .ofType(String.class);
    OptionSpec<String> file =
        p.accepts("schema-file", "Schema File")
        .withOptionalArg()
        .ofType(String.class);
    OptionSpec<String> inschema =
        p.accepts("schema", "Schema")
        .withOptionalArg()
        .ofType(String.class);
    OptionSet opts = p.parse(args.toArray(new String[0]));

    List<String> nargs = opts.nonOptionArguments();
    if (nargs.size() != 1) {
      err.println("Expected 1 arg: input_file");
      p.printHelpOn(err);
      return 1;
    }
    String schemastr = inschema.value(opts);
    String schemafile = file.value(opts);
    if (schemastr == null && schemafile == null) {
        err.println("Need an input schema file (--schema-file) or inline schema (--schema)");
        p.printHelpOn(err);
        return 1;
    }
    if (schemafile != null) {
        schemastr = readSchemaFromFile(schemafile);
    }
    
    Schema schema = Schema.parse(schemastr);
    DatumReader<Object> reader = new GenericDatumReader<Object>(schema);

    InputStream input = Util.fileOrStdin(nargs.get(0), stdin);
    try {
      DataInputStream din = new DataInputStream(input);
      DataFileWriter<Object> writer =
        new DataFileWriter<Object>(new GenericDatumWriter<Object>());
      writer.setCodec(CodecFactory.fromString(codec.value(opts)));
      writer.create(schema, out);
      Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);
      Object datum;
      while (true) {
        try {
          datum = reader.read(null, decoder);
        } catch (EOFException e) {
          break;
        }
        writer.append(datum);
      }
      writer.close();
    } finally {
      if (input != stdin) {
        input.close();
      }
    }
    return 0;
  }

  public static String readSchemaFromFile(String schemafile) throws IOException {
    String schemastr;
    StringBuilder b = new StringBuilder();
    FileReader r = new FileReader(schemafile);
    try {
        char[] buf = new char[64*1024];
        for(;;) {
            int read = r.read(buf);
            if (read==-1) break;
            b.append(buf, 0, read);
        }
        schemastr = b.toString();
    } finally {
        r.close();
    }
    return schemastr;
  }
}
