package org.flowforwarding.of.controller.rest;

import org.restlet.resource.ServerResource;

/**
 * Root resource implementation.
 */
public class RootServerResource extends ServerResource implements RootResource {

    public String represent() {
        return "Welcome to the " + getApplication().getName() + " !";
    }

}
