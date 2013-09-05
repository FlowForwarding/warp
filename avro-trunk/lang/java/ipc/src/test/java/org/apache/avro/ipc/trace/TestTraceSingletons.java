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

package org.apache.avro.ipc.trace;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.junit.Before;

public class TestTraceSingletons {
  
  @Before
  public void clearSingleton() throws Exception {
    SingletonTestingTracePlugin.clearSingletonInfo();
  }

  @Test
  public void testNormalConfiguration() throws IOException {
    TracePluginConfiguration conf = new TracePluginConfiguration();
    SingletonTestingTracePlugin.configureSingleton(conf);
    TracePlugin plugin = SingletonTestingTracePlugin.getSingleton();
    assertEquals(plugin.config, conf);
  }
  
  /** Someone tries to re-configure after plugin dispatched. */
  @Test(expected = RuntimeException.class)
  public void testInvalidDoubleConfiguration() throws IOException {
    TracePluginConfiguration conf1 = new TracePluginConfiguration();
    TracePluginConfiguration conf2 = new TracePluginConfiguration();
    conf2.clientPort = 3333;
    SingletonTestingTracePlugin.configureSingleton(conf1);
    TracePlugin plugin = SingletonTestingTracePlugin.getSingleton();
    SingletonTestingTracePlugin.configureSingleton(conf2);
  }
  
  /** 
   * Someone tries to re-configure after plugin dispatched, but config is 
   * the same.
   */
  @Test
  public void testValidDoubleConfiguration() throws IOException {
    TracePluginConfiguration conf1 = new TracePluginConfiguration();
    TracePluginConfiguration conf2 = new TracePluginConfiguration();
    SingletonTestingTracePlugin.configureSingleton(conf1);
    TracePlugin plugin = SingletonTestingTracePlugin.getSingleton();
    try {
      SingletonTestingTracePlugin.configureSingleton(conf2);
    }
    catch (RuntimeException e) {
      throw new AssertionError("Valid double configuration threw error.");
    }
  }
  
  /**
   * Someone never configures the singleton, then asks for one.
   */
  @Test(expected = RuntimeException.class)
  public void testNoConfiguration() throws IOException {
    TracePlugin plugin = SingletonTestingTracePlugin.getSingleton();
  }
}
