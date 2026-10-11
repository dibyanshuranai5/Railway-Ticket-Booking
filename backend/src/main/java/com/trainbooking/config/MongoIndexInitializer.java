package com.trainbooking.config;

import com.trainbooking.auth.model.User;
import com.trainbooking.ticket.model.Ticket;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexInitializer {
    private final MongoTemplate mongoTemplate;
    private volatile boolean initialized;

    public MongoIndexInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public synchronized void ensureIndexes() {
        if (initialized) {
            return;
        }
        mongoTemplate.indexOps(User.class).ensureIndex(
                new Index().on("email", Sort.Direction.ASC).unique().named("uq_users_email"));
        mongoTemplate.indexOps(Ticket.class).ensureIndex(
                new Index().on("pnr", Sort.Direction.ASC).unique().named("uq_tickets_pnr"));
        initialized = true;
    }
}
