package com.fidectus.eventlog.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_log")
@EntityListeners(AuditingEntityListener.class)
public class EventLog implements Serializable {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_log_id_seq")
//    @SequenceGenerator(name = "event_log_id_seq", sequenceName = "event_log_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private EventType type;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "hash")
    private String hash;

    public EventLog() {
    }

    public EventLog(@NotNull EventType type, @NotNull Long userId, @NotNull LocalDateTime createdDate, String hash) {
        this.type = type;
        this.userId = userId;
        this.createdDate = createdDate;
        this.hash = hash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
