package si.fri.rso.uniborrow.cash.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import si.fri.rso.uniborrow.cash.models.entities.CashEntity;
import si.fri.rso.uniborrow.cash.models.entities.TransactionEntity;
import si.fri.rso.uniborrow.cash.services.beans.CashDataProviderBean;
import si.fri.rso.uniborrow.cash.services.currencyconverter.CurrencyConverterService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/cash")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CashDataResource {

    private Logger log = Logger.getLogger(CashDataResource.class.getName());

    @Inject
    private CurrencyConverterService currencyConverterService;

    @Inject
    private CashDataProviderBean cashDataProviderBean;

    @GET
    @Path("/{userId}")
    public Response getCashByUserId(@PathParam("userId") Integer cashId, @QueryParam("currency") String currency) {
        CashEntity cashEntity = cashDataProviderBean.getCashByUserId(cashId);
        float convertedCash = currencyConverterService.convertCash(cashEntity.getCurrentCash(), "EUR", currency);
        cashEntity.setCurrentCash(convertedCash);
        return Response.status(Response.Status.OK).entity(cashEntity).build();
    }

    @POST
    @Path("/{userId}/add")
    public Response acceptCash(@PathParam("userId") Integer userId,
                               @QueryParam("amount") Float amount, @QueryParam("currency") String currency) {
        float convertedCash = currencyConverterService.convertCash(amount, "EUR", currency);
        CashEntity acceptedCash = cashDataProviderBean.addCash(userId, convertedCash);
        if (acceptedCash == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(acceptedCash).build();
    }

    @POST
    @Path("/{userId}/withdraw")
    public Response withdrawCash(@PathParam("userId") Integer userId,
                                 @QueryParam("amount") Float amount,
                                 @QueryParam("currency") String currency) {
        float convertedCash = currencyConverterService.convertCash(amount, "EUR", currency);
        CashEntity acceptedCash = cashDataProviderBean.addCash(userId, -convertedCash);
        if (acceptedCash == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(acceptedCash).build();
    }

    @POST
    @Path("/{fromUserId}/send/{toUserId}")
    public Response sendCash(@PathParam("fromUserId") Integer fromUserId, @PathParam("toUserId") Integer toUserId,
                             @QueryParam("amount") Float amount,
                             @QueryParam("currency") String currency) {
        float convertedCash = currencyConverterService.convertCash(amount, "EUR", currency);
        TransactionEntity transactionEntity = cashDataProviderBean.sendCash(fromUserId, toUserId, convertedCash);
        if (transactionEntity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(transactionEntity).build();
    }

    @GET
    @Path("/transactions/{userId}")
    public Response getUserTransactions(@PathParam("userId") Integer userId) {
        List<TransactionEntity> transactionEntityList = cashDataProviderBean.getCashTransactionsByUserId(userId);
        return Response.status(Response.Status.OK).entity(transactionEntityList).build();
    }
}