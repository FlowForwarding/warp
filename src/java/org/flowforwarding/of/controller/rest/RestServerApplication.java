package org.flowforwarding.of.controller.rest;

import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;

/**
 * The reusable mail server application.
 */
public class RestServerApplication extends Application {

    /**
     * Constructor.
     */
    public RestServerApplication() {
       
        setName("RESTful OpenFlow controller service");
        setDescription("NA");
        setOwner("FlowForwarding");
        setAuthor("Dmitry Orekhov");
    }

    /**
     * Creates a root Router to dispatch call to server resources.
     */
    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/", RootServerResource.class);
        router.attach("/accounts/", AccountsServerResource.class);
        router.attach("/accounts/{accountId}", AccountServerResource.class);
        return router;
    }
    
    public static void main (String[] args){

       Server restServer = new Server(Protocol.HTTP, 8080);
       restServer.setNext(new RestServerApplication());
       try {
          restServer.start();
       } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
       }
    }

}
