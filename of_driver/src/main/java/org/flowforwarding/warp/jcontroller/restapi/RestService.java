package org.flowforwarding.warp.jcontroller.restapi;

import org.flowforwarding.warp.jcontroller.Controller;
import org.flowforwarding.warp.jcontroller.Controller.ControllerRef;
import org.flowforwarding.warp.demo.SimpleHandler;

public class RestService {

   public static void main(String[] args) {
      ControllerRef cRef = Controller.launch(SimpleHandler.class); 
      
      RestApiServer restApi =  new RestApiServer(cRef);
      restApi.run();
   }

}
