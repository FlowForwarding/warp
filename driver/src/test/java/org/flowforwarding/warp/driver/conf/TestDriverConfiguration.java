/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.driver.conf;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author EPAM Systems
 */
public class TestDriverConfiguration{
   
   protected static void setEnv(Map<String, String> newenv)
   {
      try{
         Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
         Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
         theEnvironmentField.setAccessible(true);
         Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
         env.putAll(newenv);
         Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
         theCaseInsensitiveEnvironmentField.setAccessible(true);
         Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
         cienv.putAll(newenv);
      } catch (NoSuchFieldException e) {
         try {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for(Class cl : classes) {
               if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                  Field field = cl.getDeclaredField("m");
                  field.setAccessible(true);
                  Object obj = field.get(env);
                  Map<String, String> map = (Map<String, String>) obj;
                  map.clear();
                  map.putAll(newenv);
               }
            }
         } catch (Exception e2) {
           e2.printStackTrace();
         } 
      } catch (Exception e1) {
           e1.printStackTrace();
      } 
   }
   
   @Before
   public void setUp() {}
   
   @After
   public void cleanUp() {}
   
   @Test
   public void testWrongWarpConfig () {
      System.out.println("WARP_HOME: the OLD one:" + System.getenv("WARP_HOME"));
      Map<String, String> newenv = new HashMap<String, String>();
      newenv.put("WARP_HOME", "MWA-HA-HA!!!");
      setEnv(newenv);
      System.out.println("WARP_HOME: the NEW one:" + System.getenv("WARP_HOME"));
      
   }
}
