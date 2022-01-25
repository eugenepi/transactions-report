package com.natwest.transactions.report.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateResponse {

    @JsonProperty(value = "result")
    private String result;
    @JsonProperty(value = "conversion_rates")
    private Map<String, Double> conversionRates;

    public ExchangeRateResponse(String result, Map<String, Double> conversionRates) {
        this.result = result;
        this.conversionRates = conversionRates;
    }

    public ExchangeRateResponse() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Map<String, Double> getConversionRates() {
        return conversionRates;
    }

    public void setConversionRates(Map<String, Double> conversionRates) {
        this.conversionRates = conversionRates;
    }
}
