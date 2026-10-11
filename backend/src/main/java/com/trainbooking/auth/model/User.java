package com.trainbooking.auth.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    public String id;

    public String name;

    @Indexed(unique = true)
    public String email;

    public String phone;
    public String passwordHash;
    public Instant createdAt;

    public User() {
    }

    public User(String name, String email, String phone, String passwordHash) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }
}
