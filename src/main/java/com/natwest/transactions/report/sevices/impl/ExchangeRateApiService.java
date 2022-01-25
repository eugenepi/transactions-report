package com.natwest.transactions.report.sevices.impl;

import com.natwest.transactions.report.model.ExchangeRateResponse;
import com.natwest.transactions.report.sevices.CurrencyExchangeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

public class ExchangeRateApiService implements CurrencyExchangeService {

  private final RestTemplate restTemplate;
  private final String restApiKey;

  private static final String URL = "https://v6.exchangerate-api.com/v6/";
  private final Logger logger = LogManager.getLogger(ExchangeRateApiService.class);

  public ExchangeRateApiService(RestTemplate restTemplate, String restApiKey) {
    this.restTemplate = restTemplate;
    this.restApiKey = restApiKey;
  }

  @Override
  public Optional<Map<String, Double>> getExchangeRate(String currency) {
    ResponseEntity<ExchangeRateResponse> rateResponse;

    try {
      rateResponse =
          this.restTemplate.getForEntity(
              URL + restApiKey + "/latest/" + currency, ExchangeRateResponse.class);
    } catch (Exception e) {
      logger.warn("Error response from Exchange service. Reason is: ", e);
      return Optional.empty();
    }

    if (rateResponse.getStatusCode() == HttpStatus.OK && rateResponse.hasBody()) {
      ExchangeRateResponse response = rateResponse.getBody();
      if (response != null && "success".equals(response.getResult())) {
        return Optional.of(response.getConversionRates());
      }
    }
    return Optional.empty();
  }
}
