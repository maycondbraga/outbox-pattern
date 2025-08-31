package com.md.payments.commons.dtos;

import com.md.payments.commons.enums.StatusEvent;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class OutboxEvent {

    private String messageId;
    private String createdAt;
    private String payload;
    private StatusEvent status;
    private String error;
    private String processedAt;

    public OutboxEvent() {
    }

    @DynamoDbPartitionKey
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @DynamoDbSortKey
    @DynamoDbSecondarySortKey(indexNames = "status-index")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "status-index")
    public String getStatus() {
        return status != null ? status.name() : null;
    }

    public void setStatus(String status) {
        this.status = status != null ? StatusEvent.valueOf(status) : null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }
}
