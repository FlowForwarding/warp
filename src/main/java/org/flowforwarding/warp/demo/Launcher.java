/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.demo;

import java.util.Set;

import org.flowforwarding.warp.context.Context;
import org.flowforwarding.warp.jcontroller.Controller;
import org.flowforwarding.warp.jcontroller.Controller.ControllerRef;

public class Launcher {

   /**
    * @param args
    */
   public static void main(String[] args) {
      Context context = Context.getInstance();
      
      Set<String> protocols = context.protocols();
      for (String p : protocols) {
         System.out.println(p + ": ");
        
         for (String key : context.protocolProperties(p).keySet()) {
            System.out.println(key + ": " + context.protocolProperties(p).get(key));
         }
      }
      
      ControllerRef cRef = Controller.launch(SimpleHandler.class);
   }

}
