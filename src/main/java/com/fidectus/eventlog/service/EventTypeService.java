package com.fidectus.eventlog.service;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.repository.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class EventTypeService {

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private EventLogService eventLogService;

    public abstract EventType getType();

    public abstract EventLog save(EventLog eventLog);

    protected EventLog saveEvent(EventLog event) {
        event = eventLogRepository.save(event);
        eventLogService.hash(event);
        return event;
    }

    protected abstract void validate(EventLog eventLog);
}
