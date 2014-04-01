/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.warp.jcontroller.restapi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.flowforwarding.warp.jcontroller.Controller.ControllerRef;
import org.flowforwarding.warp.jcontroller.ControllerOld.ObserverTask;
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
      
      Map<String, Map<String, Object>> entries = (Map<String, Map<String, Object>>)getContext().getAttributes().get("entries");
      //Map<String, Object> entries = (Map<String, Object>)getContext().getAttributes().get("entries");
      ForkJoinPool pool = (ForkJoinPool) getContext().getAttributes().get("pool");
      ObserverTask<Integer, RestApiTask> observerTask = (ObserverTask<Integer, RestApiTask>) getContext().getAttributes().get("observerTask");
      ControllerRef controllerRef =  (ControllerRef)getContext().getAttributes().get("controllerRef");
      ForkJoinTask<Map<String, Map<String, Object>>> task2; 
      

      if (entries == null)
         entries = new HashMap<>();
         
      
      if (isDelete) {
         Map <String, Object> delete = new HashMap();
         delete.put("DELETE", "YES");
         entries.put(jsonRequest, delete);
      }
      
      RecursiveTask <Map<String, Map<String, Object>>> task = new RestApiTask(jsonRequest, entries);
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
