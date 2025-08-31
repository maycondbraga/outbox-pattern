package com.md.payments.processevents.repositories;

import com.md.payments.commons.dtos.ProcessedMessage;
import com.md.payments.commons.enums.StatusEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Instant;

@Repository
public class ProcessedMessageRepository {
    private final DynamoDbEnhancedClient dynamo;
    private DynamoDbTable<ProcessedMessage> table;

    public ProcessedMessageRepository(DynamoDbEnhancedClient dynamo) {
        this.dynamo = dynamo;
    }

    @PostConstruct
    public void init() {
        table = dynamo.table("processed_messages", TableSchema.fromBean(ProcessedMessage.class));
    }

    public boolean isProcessed(String messageId) {
        return table.getItem(r -> r.key(k -> k.partitionValue(messageId))) != null;
    }

    public void markProcessed(String messageId) {
        ProcessedMessage pm = new ProcessedMessage();
        pm.setMessageId(messageId);
        pm.setStatus(StatusEvent.PROCESSED);
        pm.setProcessedAt(Instant.now().toString());
        table.putItem(pm);
    }

    public void reset(String messageId) {
        ProcessedMessage pm = new ProcessedMessage();
        pm.setMessageId(messageId);
        pm.setStatus(StatusEvent.PENDING);
        pm.setProcessedAt(null);
        table.putItem(pm);
    }
}
