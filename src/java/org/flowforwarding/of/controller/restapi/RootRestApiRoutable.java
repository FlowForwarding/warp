package org.flowforwarding.of.controller.restapi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.flowforwarding.of.controller.ControllerOld.ObserverTask;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class RootRestApiRoutable implements RestletRoutable {
   
  // protected Map<String, Map<String, OFFlowMod>> entries;
   protected Map<String, Map<String, Object>> entries;
   protected ForkJoinPool pool;
   protected ObserverTask<Integer, RestApiTask> observerTask;
   
   public RootRestApiRoutable(ForkJoinPool pl, ObserverTask<Integer, RestApiTask> task) {
 //     this.entries = new HashMap<String, Map<String, OFFlowMod>>();
      this.entries = new HashMap<String, Map<String, Object>>();
      this.pool = pl;
      this.observerTask = task;
   }

   @Override
   public Restlet getRestlet(Context context) {
       Map<String, Object> attributes = new HashMap<String, Object>();
       
       attributes.put("entries", entries);
       attributes.put("pool", pool);
       attributes.put("observerTask", observerTask);
       
       context.setAttributes(attributes);
       Router router = new Router(context);
       router.attach("/restapi", RootRestApiResource.class);
       /*router.attach("/clear/{switch}/json", ClearStaticFlowEntriesResource.class);
       router.attach("/list/{switch}/json", ListStaticFlowEntriesResource.class);*/
       return router;
   }

   /**
    * Set the base path for the Topology
    */
   @Override
   public String basePath() {
//       return "/wm/staticflowentrypusher";
       return "/ff/of/controller";
   }

}
