package si.fri.rso.uniborrow.cash.api.v1.resources;

import si.fri.rso.uniborrow.cash.services.config.AdministrationProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@ApplicationScoped
@Path("/break")
public class ServerBreakingResource {
    @Inject
    private AdministrationProperties administrationProperties;

    @Path("/server")
    @GET
    public String breakServer(@QueryParam("break") boolean doBreak) {
        administrationProperties.setBroken(doBreak);
        if (doBreak) {
            return "GREAT, YA BROKE IT";
        } else {
            return "CHANGED YOUR MIND EH?";
        }
    }

    @Path("/exchangeApi")
    @GET
    public String breakExchangeApi(@QueryParam("break") boolean doBreak) {
        administrationProperties.setBrokenExchangeApi(doBreak);
        if (doBreak) {
            return "GREAT, YA BROKE IT";
        } else {
            return "CHANGED YOUR MIND EH?";
        }
    }
}
