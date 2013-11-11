package org.flowforwarding.warp.controller.restapi;

import org.flowforwarding.warp.controller.Controller;
import org.flowforwarding.warp.controller.Controller.ControllerRef;
import org.flowforwarding.warp.demo.SimpleHandler;

public class RestService {

   public static void main(String[] args) {
      ControllerRef cRef = Controller.launch(SimpleHandler.class);      
   }

}
