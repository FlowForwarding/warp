/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.jcontroller.restapi;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.flowforwarding.warp.jcontroller.Controller.ControllerRef;
import org.flowforwarding.warp.jcontroller.ControllerOld.ObserverTask;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.service.StatusService;

/**
 * @author Infoblox Inc.
 *
 */
public class RestApiServer  {
    //protected static Logger logger = LoggerFactory.getLogger(RestApiServer.class);
    protected List<org.flowforwarding.warp.jcontroller.restapi.RestletRoutable> restlets;
    protected int restPort = 8080;
    protected ForkJoinPool pool = null;
    
    @Deprecated
    public RestApiServer(ForkJoinPool pool, ObserverTask<Integer, RestApiTask> observerTask) {
       this.restlets = new ArrayList<org.flowforwarding.warp.jcontroller.restapi.RestletRoutable>();
       org.flowforwarding.warp.jcontroller.restapi.RestletRoutable routable = new org.flowforwarding.warp.jcontroller.restapi.RootRestApiRoutable(pool, observerTask);
       restlets.add(routable);
    }
    
    public RestApiServer(ControllerRef cRef) {
       this.restlets = new ArrayList<org.flowforwarding.warp.jcontroller.restapi.RestletRoutable>();
       this.pool = new ForkJoinPool();
       org.flowforwarding.warp.jcontroller.restapi.RestletRoutable routable = new org.flowforwarding.warp.jcontroller.restapi.RootRestApiRoutable(cRef, this.pool);
       restlets.add(routable);
    }
    
    protected class RestApplication extends Application {
        protected Context context;
        
        public RestApplication() {
            super(new Context());
            this.context = getContext();
        }
        
        @Override
        public Restlet createInboundRoot() {
            Router baseRouter = new Router(context);
            baseRouter.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
            for (org.flowforwarding.warp.jcontroller.restapi.RestletRoutable rr : restlets) {
                baseRouter.attach(rr.basePath(), rr.getRestlet(context));
            }

            Filter slashFilter = new Filter() {            
                @Override
                protected int beforeHandle(Request request, Response response) {
                    Reference ref = request.getResourceRef();
                    String originalPath = ref.getPath();
                    if (originalPath.contains("//"))
                    {
                        String newPath = originalPath.replaceAll("/+", "/");
                        ref.setPath(newPath);
                    }
                    return Filter.CONTINUE;
                }

            };
            slashFilter.setNext(baseRouter);
            
            return slashFilter;
        }
        
        public void run(int restPort) {
            setStatusService(new StatusService() {
                @Override
                public Representation getRepresentation(Status status,
                                                        Request request,
                                                        Response response) {
                    return new JacksonRepresentation<Status>(status);
                }                
            });
            
            try {
                final Component component = new Component();
                component.getServers().add(Protocol.HTTP, restPort);
                component.getClients().add(Protocol.CLAP);
                component.getDefaultHost().attach(this);
                component.start();
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    // @Override
    public void addRestletRoutable(org.flowforwarding.warp.jcontroller.restapi.RestletRoutable routable) {
        restlets.add(routable);
    }

    // @Override
    public void run() {
        
        RestApplication restApp = new RestApplication();
        restApp.run(restPort);
    }
}