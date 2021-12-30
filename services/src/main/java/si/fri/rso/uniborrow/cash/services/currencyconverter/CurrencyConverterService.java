package si.fri.rso.uniborrow.cash.services.currencyconverter;

import si.fri.rso.uniborrow.cash.services.users.UsersService;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@RequestScoped
public class CurrencyConverterService {

    private Logger log = Logger.getLogger(UsersService.class.getName());

    private WebTarget webTarget = ClientBuilder.newClient().target("https://currency-converter5.p.rapidapi.com/currency/convert");

    public float convertCash(float cash, String currencyFrom, String currencyTo) {
        Float response = webTarget.
                queryParam("from", currencyFrom).
                queryParam("to", currencyTo).
                queryParam("q", cash).
                request(MediaType.APPLICATION_JSON_TYPE).
                header("x-rapidapi-host", "currency-exchange.p.rapidapi.com").
                header("x-rapidapi-key", "ea575f1580msh636fdf0dbab5542p1aa20cjsnd7d2d48fd4b0").
                buildGet().
                invoke(Float.class);
        return response;
    }
}
