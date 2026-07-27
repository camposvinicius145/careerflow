package com.careerflow.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected UserAccount() {}

    private UserAccount(
            UUID id, String email, String displayName, String passwordHash, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.email = normalizeEmail(email);
        this.displayName = Objects.requireNonNull(displayName).trim();
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static UserAccount create(String email, String displayName, String passwordHash) {
        return new UserAccount(UUID.randomUUID(), email, displayName, passwordHash, Instant.now());
    }

    private static String normalizeEmail(String email) {
        return Objects.requireNonNull(email).trim().toLowerCase(Locale.ROOT);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
