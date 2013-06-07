package org.flowforwarding.of.controller.rest;


import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Implementation of a mail account resource.
 */
public class AccountServerResource extends ServerResource implements
        AccountResource {

    /** The account identifier. */
    private int accountId;

    /**
     * Retrieve the account identifier based on the URI path variable
     * "accountId" declared in the URI template attached to the application
     * router.
     */
    @Override
    protected void doInit() throws ResourceException {
        this.accountId = Integer.parseInt(getAttribute("accountId"));
    }

    public String represent() {
        return AccountsServerResource.getAccounts().get(this.accountId);
    }

    public void store(String account) {
        AccountsServerResource.getAccounts().set(this.accountId, account);
    }

    public void remove() {
        AccountsServerResource.getAccounts().remove(this.accountId);
    }
}
