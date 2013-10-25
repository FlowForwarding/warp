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

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;

/** Reads a data file and dumps to JSON */
public class DataFileReadTool implements Tool {

  @Override
  public String getName() {
    return "tojson";
  }

  @Override
  public String getShortDescription() {
    return "Dumps an Avro data file as JSON, one record per line.";
  }

  @Override
  public int run(InputStream stdin, PrintStream out, PrintStream err,
      List<String> args) throws Exception {
    if (args.size() != 1) {
      // Unlike other commands, "-" can't be used for stdin, because
      // we can only use seekable files.
      err.println("Expected 1 argument: input_file.");
      return 1;
    }

    GenericDatumReader<Object> reader = new GenericDatumReader<Object>();
    FileReader<Object> fileReader =
      DataFileReader.openReader(new File(args.get(0)), reader);
    try {
      Schema schema = fileReader.getSchema();
      DatumWriter<Object> writer = new GenericDatumWriter<Object>(schema);
      JsonEncoder encoder = EncoderFactory.get().jsonEncoder(schema, out);
      for (Object datum : fileReader)
        writer.write(datum, encoder);
      encoder.flush();
      out.println();
      out.flush();
    } finally {
      fileReader.close();
    }
    return 0;
  }

}
