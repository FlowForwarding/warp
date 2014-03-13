/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.demo;

import org.flowforwarding.warp.jcontroller.Controller;
import org.flowforwarding.warp.jcontroller.Controller.ControllerRef;

public class Launcher {

   /**
    * @param args
    */
   public static void main(String[] args) {
      ControllerRef cRef = Controller.launch(SimpleHandler.class);
   }

}
