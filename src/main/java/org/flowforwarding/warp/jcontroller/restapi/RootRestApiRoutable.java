/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.warp.jcontroller.restapi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.flowforwarding.warp.jcontroller.Controller.ControllerRef;
import org.flowforwarding.warp.jcontroller.ControllerOld.ObserverTask;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;


/**
 * @author Infoblox Inc.
 *
 */
public class RootRestApiRoutable implements RestletRoutable {
   
  // protected Map<String, Map<String, OFFlowMod>> entries;
   protected Map<String, Map<String, Object>> entries;
   protected ForkJoinPool pool;
   protected ObserverTask<Integer, org.flowforwarding.warp.jcontroller.restapi.RestApiTask> observerTask;
   protected ControllerRef controllerRef;
   
   @Deprecated
   public RootRestApiRoutable(ForkJoinPool pl, ObserverTask<Integer, org.flowforwarding.warp.jcontroller.restapi.RestApiTask> task) {
 //     this.entries = new HashMap<String, Map<String, OFFlowMod>>();
      this.entries = new HashMap<String, Map<String, Object>>();
      this.pool = pl;
      this.observerTask = task;
   }
   
   public RootRestApiRoutable(ControllerRef cRef, ForkJoinPool pool) {
 //     this.entries = new HashMap<String, Map<String, OFFlowMod>>();
      this.entries = null;
      this.pool = pool;
      this.observerTask = null;
      this.controllerRef = cRef;
   }

   @Override
   public Restlet getRestlet(Context context) {
       Map<String, Object> attributes = new HashMap<String, Object>();
       
       if (entries != null) 
          attributes.put("entries", entries);
       if (pool != null)
          attributes.put("pool", pool);
       if (observerTask != null)
          attributes.put("observerTask", observerTask);
       if (controllerRef != null)
          attributes.put("controllerRef", controllerRef);
              
       context.setAttributes(attributes);
       Router router = new Router(context);
       router.attach("/restapi", RootRestApiResource.class);
       return router;
   }

   /**
    * Set the base path for the Topology
    */
   @Override
   public String basePath() {
       return "/ff/of/controller";
   }

}
