package com.fidectus.eventlog.service;

import com.fidectus.eventlog.domain.EventLog;

import java.util.List;
import java.util.Optional;

public interface EventLogService {

    List<EventLog> getEventByUserIdOrderById(long userId);

    void hash(EventLog event);

    Optional<EventLog> getLastEventByUserId(Long userId);
}
