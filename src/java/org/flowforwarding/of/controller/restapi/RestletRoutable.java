package org.flowforwarding.of.controller.restapi;

import org.restlet.Context;
import org.restlet.Restlet;

/**
 * Register a set of REST resources with the central controller
 * @author readams
 */
public interface RestletRoutable {
    /**
     * Get the restlet that will map to the resources
     * @param context the context for constructing the restlet
     * @return the restlet
     */
    Restlet getRestlet(Context context);
    
    /**
     * Get the base path URL where the router should be registered
     * @return the base path URL where the router should be registered
     */
    String basePath();
}
