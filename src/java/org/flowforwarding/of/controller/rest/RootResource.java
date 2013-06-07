package org.flowforwarding.of.controller.rest;
import org.restlet.resource.Get;

/**
 * Root resource.
 */
public interface RootResource {

    /**
     * Represents the application root with a welcome message.
     * 
     * @return The root representation.
     */
    @Get("txt")
    public String represent();

}
