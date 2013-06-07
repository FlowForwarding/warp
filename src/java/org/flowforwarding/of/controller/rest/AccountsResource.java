package org.flowforwarding.of.controller.rest;


import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Collection resource containing user accounts.
 */
public interface AccountsResource {

    /**
     * Returns the list of accounts, each one on a separate line.
     * 
     * @return The list of accounts.
     */
    @Get("txt")
    public String represent();

    /**
     * Add the given account to the list and returns its position as an
     * identifier.
     * 
     * @param account
     *            The account to add.
     * @return The account identifier.
     */
    @Post("txt")
    public String add(String account);

}
