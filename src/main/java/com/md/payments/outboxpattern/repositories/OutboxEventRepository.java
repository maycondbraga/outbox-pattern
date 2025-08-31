package com.md.payments.outboxpattern.repositories;

import com.md.payments.commons.dtos.OutboxEvent;
import com.md.payments.commons.enums.StatusEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OutboxEventRepository {
    private final DynamoDbEnhancedClient dynamo;
    private DynamoDbTable<OutboxEvent> table;

    public OutboxEventRepository(DynamoDbEnhancedClient dynamo) {
        this.dynamo = dynamo;
    }

    @PostConstruct
    public void init() {
        table = dynamo.table("outbox_events", TableSchema.fromBean(OutboxEvent.class));
    }

    public void save(OutboxEvent event) {
        table.putItem(event);
    }

    public List<OutboxEvent> findPending() {
        String gsiName = "status-index";
        DynamoDbIndex<OutboxEvent> index = table.index(gsiName);

        Key key = Key.builder()
                .partitionValue(StatusEvent.PENDING.name())
                .build();

        List<OutboxEvent> result = new ArrayList<>();

        index.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)).limit(10))
                .stream()
                .forEach(page -> result.addAll(page.items()));

        return result;
    }
}
