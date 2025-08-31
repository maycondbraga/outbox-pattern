package com.md.payments.processevents.consumers;

import com.md.payments.processevents.repositories.ProcessedMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OutboxKafkaConsumer {
    private final ProcessedMessageRepository processedMessageRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxKafkaConsumer.class);

    public OutboxKafkaConsumer(ProcessedMessageRepository processedMessageRepository) {
        this.processedMessageRepository = processedMessageRepository;
    }

    @KafkaListener(topics = "events", groupId = "events-group")
    public void processar(@Header(KafkaHeaders.RECEIVED_KEY) String messageId,
                          @Payload String payload) {

        if (processedMessageRepository.isProcessed(messageId)) {
            LOGGER.info("Mensagem já processada: {}", messageId);
            return;
        }

        try {
            LOGGER.info("Processando: {}", payload);
            processedMessageRepository.markProcessed(messageId);
        } catch (Exception e) {
            LOGGER.error("Falha ao processar: {}", e.getMessage());
        }
    }
}
