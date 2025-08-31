package com.md.payments.outboxpattern.controllers;

import com.md.payments.commons.dtos.OutboxEvent;
import com.md.payments.commons.enums.StatusEvent;
import com.md.payments.outboxpattern.repositories.OutboxEventRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
public class EventController {

    private final OutboxEventRepository outboxEventRepository;

    public EventController(OutboxEventRepository outboxRepo) {
        this.outboxEventRepository = outboxRepo;
    }

    @PostMapping("/events")
    public String criar(@RequestBody String payload) {
        String messageId = UUID.randomUUID().toString();
        String createdAt = Instant.now().toString();

        OutboxEvent event = new OutboxEvent();
        event.setMessageId(messageId);
        event.setCreatedAt(createdAt);
        event.setPayload(payload);
        event.setStatus(StatusEvent.PENDING.name());

        outboxEventRepository.save(event);
        return messageId;
    }
}