package si.fri.rso.uniborrow.cash.services.currencyconverter;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rso.uniborrow.cash.services.config.AdministrationProperties;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.time.temporal.ChronoUnit;

@RequestScoped
public class CurrencyConverterService {

    private final WebTarget webTarget = ClientBuilder.newClient().target("https://currency-exchange.p.rapidapi.com/exchange");
    private final WebTarget webTarget2 = ClientBuilder.newClient().target("https://exchangerate-api.p.rapidapi.com/rapid/latest");
    @Inject
    private AdministrationProperties administrationProperties;

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "convertCashFallback")
    @Retry(maxRetries = 3)
    public float convertCash(float cash, String currencyFrom, String currencyTo) {
        if (administrationProperties.getBrokenExchangeApi()) {
            throw new InternalServerErrorException();
        }
        Float response = webTarget.
                queryParam("from", currencyFrom).
                queryParam("to", currencyTo).
                request(MediaType.APPLICATION_JSON_TYPE).
                header("x-rapidapi-host", "currency-exchange.p.rapidapi.com").
                header("x-rapidapi-key", "ea575f1580msh636fdf0dbab5542p1aa20cjsnd7d2d48fd4b0").
                buildGet().
                invoke(Float.class);
        return response * cash;
    }

    public float convertCashFallback(float cash, String currencyFrom, String currencyTo) {
        Rates response = webTarget2.
                path(currencyFrom).
                request(MediaType.APPLICATION_JSON_TYPE).
                header("x-rapidapi-host", "exchangerate-api.p.rapidapi.com").
                header("x-rapidapi-key", "ea575f1580msh636fdf0dbab5542p1aa20cjsnd7d2d48fd4b0").
                buildGet().
                invoke(Rates.class);
        return response.getRates().get(currencyTo) * cash;
    }


}
