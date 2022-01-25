package com.natwest.transactions.report.configuration;

import com.natwest.transactions.report.converter.TransactionToTransactionEntityConverter;
import com.natwest.transactions.report.repositories.TransactionRepository;
import com.natwest.transactions.report.sevices.CurrencyExchangeService;
import com.natwest.transactions.report.sevices.KafkaConsumerService;
import com.natwest.transactions.report.sevices.TransactionService;
import com.natwest.transactions.report.sevices.impl.ExchangeRateApiService;
import com.natwest.transactions.report.sevices.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${exchange_rate.api_key}")
    private String exchangeApiKey;

    @Bean
    TransactionToTransactionEntityConverter transactionToTransactionEntityConverter() {
        return new TransactionToTransactionEntityConverter();
    }

    @Bean
    TransactionService transactionService(TransactionRepository transactionRepository,
                                          TransactionToTransactionEntityConverter transactionToTransactionEntityConverter,
                                          CurrencyExchangeService currencyExchangeService) {
        return new TransactionServiceImpl(
                transactionRepository, transactionToTransactionEntityConverter, currencyExchangeService);
    }

    @Bean
    KafkaConsumerService kafkaConsumerService(TransactionService transactionService) {
        return new KafkaConsumerService(transactionService);
    }

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    CurrencyExchangeService currencyExchangeService(RestTemplate restTemplate) {
        return new ExchangeRateApiService(restTemplate, exchangeApiKey);
    }
}
