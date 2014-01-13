/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.warp.controller.restapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.flowforwarding.warp.controller.Controller.ControllerRef;
import org.flowforwarding.warp.controller.ControllerOld.ObserverTask;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * @author Infoblox Inc.
 *
 */
public class RootRestApiResource  extends ServerResource {
   
   @Post
   public String store(String jsonRequest) {
      
      return getHandler(jsonRequest);
   }
   
   @Get
   public String getHandler (String jsonRequest) {
      processRequest(jsonRequest, false);
      
      return "OK";
   }
   
   @Delete
   public String deleteFlow (String jsonRequest) {
      processRequest(jsonRequest, true);      
          
      return "DELETED";
   }
   
   protected void processRequest (String jsonRequest, boolean isDelete) {
      
      //Map<String, Map<String, OFFlowMod>> entries = (Map<String, Map<String, OFFlowMod>>)getContext().getAttributes().get("entries");

      //TODO REMOVE.
      Map<String, Object> entries = (Map<String, Object>)getContext().getAttributes().get("entries");
      ForkJoinPool pool = (ForkJoinPool) getContext().getAttributes().get("pool");
      ObserverTask<Integer, RestApiTask> observerTask = (ObserverTask<Integer, RestApiTask>) getContext().getAttributes().get("observerTask");
      ControllerRef controllerRef =  (ControllerRef)getContext().getAttributes().get("controllerRef");
      ForkJoinTask<Map<String, Object>> task2; 
      

      if (entries == null)
         entries = new HashMap<String, Object>();
      
      if (isDelete) entries.put("DELETE", "YES");
      
      RecursiveTask <Map<String, Object>> task = new RestApiTask(jsonRequest, entries);
      if (observerTask !=null)
         observerTask.update((RestApiTask) task);
      
      /*if (pool != null)
         pool.execute(task);*/
      
      if (pool != null) {
         task2 = pool.submit(task);
         try {
            entries = task2.get();
         } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
}
