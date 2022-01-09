package si.fri.rso.uniborrow.cash.services.currencyconverter;

import lombok.Data;

import java.util.Map;

@Data
public class Rates {
    private Map<String, Float> rates;
}
