package org.flowforwarding.of.controller.rest;

import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;


public class RestServer extends Application {
  
  @Override
  public Restlet createInboundRoot () {
     return new Restlet () {
        @Override
        public void handle(Request request, Response responce) {
           String entity = "Method     " + request.getMethod();
           
           responce.setEntity(entity, MediaType.TEXT_PLAIN);
        }
     };
  }
  
  public void start () {
     
     Server restServer = new Server(Protocol.HTTP, 8080);
     restServer.setNext(new RestServer());
     try {
        restServer.start();
     } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
     }
  }
}