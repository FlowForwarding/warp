package org.flowforwarding.of.controller.rest;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * User account resource.
 */
public interface AccountResource {

    /**
     * Represents the account as a simple string with the owner name for now.
     * 
     * @return The account representation.
     */
    @Get("txt")
    public String represent();

    /**
     * Stores the new value for the identified account.
     * 
     * @param account
     *            The identified account.
     */
    @Put("txt")
    public void store(String account);

    /**
     * Deletes the identified account by setting its value to null.
     */
    @Delete
    public void remove();

}
