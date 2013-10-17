/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.demo;

import org.flowforwarding.warp.controller.Controller;
import org.flowforwarding.warp.controller.Controller.ControllerRef;

public class Launcher {

   /**
    * @param args
    */
   public static void main(String[] args) {
      ControllerRef cRef = Controller.launch(SimpleHandler.class);
   }

}
