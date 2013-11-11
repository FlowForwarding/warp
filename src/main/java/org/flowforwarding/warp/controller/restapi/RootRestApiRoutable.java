/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.warp.controller.restapi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.flowforwarding.warp.controller.ControllerOld.ObserverTask;
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
   protected ObserverTask<Integer, RestApiTask> observerTask;
   
   @Deprecated
   public RootRestApiRoutable(ForkJoinPool pl, ObserverTask<Integer, RestApiTask> task) {
 //     this.entries = new HashMap<String, Map<String, OFFlowMod>>();
      this.entries = new HashMap<String, Map<String, Object>>();
      this.pool = pl;
      this.observerTask = task;
   }
   
   public RootRestApiRoutable() {
 //     this.entries = new HashMap<String, Map<String, OFFlowMod>>();
      this.entries = null;
      this.pool = null;
      this.observerTask = null;
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
