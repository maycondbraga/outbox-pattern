package com.md.payments.outboxpattern.producers;

import com.md.payments.commons.dtos.OutboxEvent;
import com.md.payments.commons.enums.StatusEvent;
import com.md.payments.outboxpattern.repositories.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OutboxKafkaProducer {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafka;

    public OutboxKafkaProducer(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafka) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafka = kafka;
    }

    @Scheduled(fixedDelay = 3000)
    public void publicarPendentes() {
        List<OutboxEvent> pendentes = outboxEventRepository.findPending();

        for (OutboxEvent event : pendentes) {
            try {
                kafka.send("events", event.getMessageId(), event.getPayload()).get(5, TimeUnit.SECONDS);
                event.setStatus(StatusEvent.PROCESSED.name());
                event.setProcessedAt(Instant.now().toString());
                outboxEventRepository.save(event);
            } catch (Exception e) {
                event.setStatus(StatusEvent.FAILED.name());
                event.setError(e.getMessage());
                outboxEventRepository.save(event);
            }
        }
    }
}
