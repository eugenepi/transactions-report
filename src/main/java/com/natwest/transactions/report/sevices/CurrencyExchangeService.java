package com.natwest.transactions.report.sevices;

import java.util.Map;
import java.util.Optional;

public interface CurrencyExchangeService {

    Optional<Map<String, Double>> getExchangeRate(String currency);
}
