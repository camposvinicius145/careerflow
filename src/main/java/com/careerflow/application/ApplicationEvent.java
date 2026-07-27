package com.careerflow.application;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "application_event")
public class ApplicationEvent {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private EventType eventType;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected ApplicationEvent() {}

    public static ApplicationEvent of(
            JobApplication application, EventType eventType, String description) {
        var event = new ApplicationEvent();
        event.id = UUID.randomUUID();
        event.application = Objects.requireNonNull(application);
        event.eventType = Objects.requireNonNull(eventType);
        event.description = Objects.requireNonNull(description);
        event.occurredAt = Instant.now();
        return event;
    }

    public UUID getId() {
        return id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
