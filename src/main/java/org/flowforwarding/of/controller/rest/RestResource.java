package org.flowforwarding.of.controller.rest;

import org.restlet.resource.ServerResource;

import org.restlet.resource.Delete;
import org.restlet.resource.Post;

public class RestResource extends ServerResource {
  
  @Post
  public String push(String request) {
     
     return "OK";
  }
  
  @Delete
  public String del(String responce) {
     
     return "ERROR";
  }
     
     

}