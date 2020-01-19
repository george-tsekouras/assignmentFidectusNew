package com.fidectus.eventlog.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

public class EventLogDto {

    @Null
    private Long id;

    @NotNull
    private String type;

    @Min(1)
    @NotNull
    private Long userId;

    @Null
    private LocalDateTime createdDate;

    @Null
    private String hash;

    public EventLogDto(@NotNull String type, @NotNull Long userId, @Null LocalDateTime createdDate, @Null String hash) {
        this.type = type;
        this.userId = userId;
        this.createdDate = createdDate;
        this.hash = hash;
    }

    public EventLogDto() {
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getType() {
        return type;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getHash() {
        return hash;
    }
}
