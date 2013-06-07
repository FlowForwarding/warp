package org.flowforwarding.of.controller.restapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.flowforwarding.of.controller.Controller.ObserverTask;
import org.openflow.protocol.*;
import org.openflow.protocol.action.*;
import org.openflow.protocol.factory.BasicFactory;
import org.openflow.protocol.instruction.OFInstructionApplyActions;
import org.openflow.protocol.match.OFMatch2;
import org.openflow.util.U16;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class RootRestApiResource  extends ServerResource {
   
   @Post
   public String store(String jsonRequest) {
      
      return getHandler(jsonRequest);
   }
   
   @Get
   public String getHandler (String jsonRequest) {
      
      //Map<String, Map<String, OFFlowMod>> entries = (Map<String, Map<String, OFFlowMod>>)getContext().getAttributes().get("entries");
      Map<String, Object> entries = (Map<String, Object>)getContext().getAttributes().get("entries");
      ForkJoinPool pool = (ForkJoinPool) getContext().getAttributes().get("pool");
      ObserverTask<Integer, RestApiTask> observerTask = (ObserverTask<Integer, RestApiTask>) getContext().getAttributes().get("observerTask");

      //RecursiveTask <Map<String, Map<String, OFFlowMod>>> task = new RestApiTask(jsonRequest, entries);
      RecursiveTask <Map<String, Object>> task = new RestApiTask(jsonRequest, entries);
      // TODO add something like execute() to IServicePool
      observerTask.update((RestApiTask) task);
      pool.execute(task);
      
    
      return "OK";
   }
}
