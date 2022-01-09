package si.fri.rso.uniborrow.cash.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
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

@Log
@ApplicationScoped
@Path("/cash")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CashDataResource {

    @Inject
    private CurrencyConverterService currencyConverterService;

    @Inject
    private CashDataProviderBean cashDataProviderBean;

    @GET
    @Path("/{userId}")
    @Operation(description = "Get cash of a user.", summary = "Get cash of a user.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Cash of a user, if user doesn't exists, returns 0.",
                    content = @Content(schema = @Schema(implementation = CashEntity.class))
            )
    })
    public Response getCashByUserId(@Parameter(
            in = ParameterIn.PATH,
            description = "User ID",
            required = true
    ) @PathParam("userId") Integer cashId, @Parameter(
            in = ParameterIn.QUERY,
            description = "Currency in which cash should be represented.",
            required = true
    ) @QueryParam("currency") String currency) {
        CashEntity cashEntity = cashDataProviderBean.getCashByUserId(cashId);
        float convertedCash = currencyConverterService.convertCash(cashEntity.getCurrentCash(), "EUR", currency);
        CashEntity responseCashEntity = new CashEntity();
        responseCashEntity.setCurrentCash(convertedCash);
        responseCashEntity.setUserId(cashId);
        responseCashEntity.setId(cashEntity.getId());
        return Response.status(Response.Status.OK).entity(responseCashEntity).build();
    }

    @POST
    @Path("/{userId}/add")
    @Operation(description = "Add cash to user.", summary = "Add cash to user.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Cash succesfully added.",
                    content = @Content(schema = @Schema(implementation = CashEntity.class))
            )
    })
    public Response addCash(@Parameter(
            in = ParameterIn.PATH,
            description = "User ID",
            required = true
    ) @PathParam("userId") Integer userId,
                            @Parameter(
                                    in = ParameterIn.QUERY,
                                    description = "Amount of cash to be added.",
                                    required = true
                            ) @QueryParam("amount") Float amount, @Parameter(
            in = ParameterIn.QUERY,
            description = "Currency in which cash should be represented.",
            required = true
    ) @QueryParam("currency") String currency) {
        float convertedCash = currencyConverterService.convertCash(amount, currency, "EUR");
        CashEntity acceptedCash = cashDataProviderBean.addCash(userId, convertedCash);
        if (acceptedCash == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(acceptedCash).build();
    }

    @POST
    @Path("/{userId}/withdraw")
    @Operation(description = "Withdraw cash from user.", summary = "Withdraw cash from user.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Cash succesfully withdrawn.",
                    content = @Content(schema = @Schema(implementation = CashEntity.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User didn't have enough cash."
            )
    })
    public Response withdrawCash(@Parameter(
            in = ParameterIn.PATH,
            description = "User ID",
            required = true
    ) @PathParam("userId") Integer userId,
                                 @Parameter(
                                         in = ParameterIn.QUERY,
                                         description = "Amount of cash to be withdrawn.",
                                         required = true
                                 )
                                 @QueryParam("amount") Float amount,
                                 @Parameter(
                                         in = ParameterIn.QUERY,
                                         description = "Currency in which cash should be represented.",
                                         required = true
                                 ) @QueryParam("currency") String currency) {
        float convertedCash = currencyConverterService.convertCash(amount, currency, "EUR");
        CashEntity acceptedCash = cashDataProviderBean.addCash(userId, -convertedCash);
        if (acceptedCash == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(acceptedCash).build();
    }

    @POST
    @Path("/{fromUserId}/send/{toUserId}")
    @Operation(description = "Send cash from a user to another user.", summary = "Send cash.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Cash was succesfully sent.",
                    content = @Content(schema = @Schema(implementation = TransactionEntity.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "The sending user did not have enough cash."
            )
    })
    public Response sendCash(@Parameter(
            in = ParameterIn.PATH,
            description = "User ID from.",
            required = true
    ) @PathParam("fromUserId") Integer fromUserId, @Parameter(
            in = ParameterIn.PATH,
            description = "User ID to.",
            required = true
    ) @PathParam("toUserId") Integer toUserId,
                             @Parameter(
                                     in = ParameterIn.QUERY,
                                     description = "Amount of cash to be sent.",
                                     required = true
                             ) @QueryParam("amount") Float amount,
                             @Parameter(
                                     in = ParameterIn.QUERY,
                                     description = "Currency in which cash should be represented.",
                                     required = true
                             ) @QueryParam("currency") String currency) {
        float convertedCash = currencyConverterService.convertCash(amount, currency, "EUR");
        TransactionEntity transactionEntity = cashDataProviderBean.sendCash(fromUserId, toUserId, convertedCash);
        if (transactionEntity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(transactionEntity).build();
    }

    @GET
    @Path("/transactions/{userId}")
    @Operation(description = "Get all transactions of a user.", summary = "Get all transactions of a user.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "All transactions of a user.",
                    content = @Content(schema = @Schema(implementation = TransactionEntity.class, type = SchemaType.ARRAY))
            )
    })
    public Response getUserTransactions(@Parameter(
            in = ParameterIn.QUERY,
            description = "User ID",
            required = true
    ) @PathParam("userId") Integer userId) {
        List<TransactionEntity> transactionEntityList = cashDataProviderBean.getCashTransactionsByUserId(userId);
        return Response.status(Response.Status.OK).entity(transactionEntityList).build();
    }
}