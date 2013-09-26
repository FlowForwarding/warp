/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.demo;

import org.flowforwarding.of.controller.Controller;
import org.flowforwarding.of.controller.Controller.ControllerRef;

public class Launcher {

   /**
    * @param args
    */
   public static void main(String[] args) {
      ControllerRef cRef = Controller.launch(SimpleHandler.class);
   }

}
