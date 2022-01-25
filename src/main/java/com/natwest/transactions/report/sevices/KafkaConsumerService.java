package com.natwest.transactions.report.sevices;

import com.natwest.transactions.report.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;

public class KafkaConsumerService {

    private final TransactionService transactionService;

    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    public KafkaConsumerService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", id = "${spring.kafka.consumer.group-id}")
    public void consume(@Payload List<Transaction> messages, Acknowledgment acknowledgment) {
        try {
            this.transactionService.saveAll(messages);
            acknowledgment.acknowledge();
        } catch (Exception e ){
            LOGGER.warn("Exception during message consuming. Reason is: ", e);
        }
    }
}
