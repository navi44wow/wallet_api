package com.wallet.wallet_api.entities;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {

    private static final Map<String, BigDecimal> exchangeRates = new HashMap<>();

    static {
        // BGN to others:
        exchangeRates.put("BGN_EUR", BigDecimal.valueOf(0.51));  // 1 BGN = 0.51 EUR
        exchangeRates.put("BGN_GBP", BigDecimal.valueOf(0.44));  // 1 BGN = 0.44 GBP
        exchangeRates.put("BGN_USD", BigDecimal.valueOf(0.57));  // 1 BGN = 0.57 USD

        // EUR to others:
        exchangeRates.put("EUR_BGN", BigDecimal.valueOf(1.96));  // 1 EUR = 1.96 BGN
        exchangeRates.put("EUR_GBP", BigDecimal.valueOf(0.86));  // 1 EUR = 0.86 GBP
        exchangeRates.put("EUR_USD", BigDecimal.valueOf(1.18));  // 1 EUR = 1.18 USD

        // GBP to others:
        exchangeRates.put("GBP_BGN", BigDecimal.valueOf(2.27));  // 1 GBP = 2.27 BGN
        exchangeRates.put("GBP_EUR", BigDecimal.valueOf(1.16));  // 1 GBP = 1.16 EUR
        exchangeRates.put("GBP_USD", BigDecimal.valueOf(1.38));  // 1 GBP = 1.38 USD

        // USD to others:
        exchangeRates.put("USD_BGN", BigDecimal.valueOf(1.75));  // 1 USD = 1.75 BGN
        exchangeRates.put("USD_EUR", BigDecimal.valueOf(0.85));  // 1 USD = 0.85 EUR
        exchangeRates.put("USD_GBP", BigDecimal.valueOf(0.72));  // 1 USD = 0.72 GBP
    }


    public static BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount; // If currencies are the same, we return the original amount.
        }

        String key = fromCurrency + "_" + toCurrency;
        BigDecimal rate = exchangeRates.get(key);

        if (rate == null) {
            throw new IllegalArgumentException("Exchange rate not found for " + key);
        }

        return amount.multiply(rate);
    }
}
