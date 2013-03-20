package org.flowforwarding.of.controller.rest;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
* Creates a router to handle REST API URIs
* @author Dmitry Orekhov
*/
public class RestRoutable {

  private String rootPath = "/ff/restapi";
  
  public Restlet getRouter(Context context) {
       Router router = new Router(context);
       router.attach("/ofp", RestResource.class);

       return router;
    }
}

