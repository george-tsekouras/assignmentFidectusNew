package com.fidectus.eventlog.service.impl;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.service.EventLogService;
import com.fidectus.eventlog.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class EventRegisterService extends EventTypeService {

    @Autowired
    private EventLogService eventLogService;

    @Override
    public EventType getType() {
        return EventType.REGISTRATION;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public EventLog save(EventLog eventLog) {
        validate(eventLog);
        return saveEvent(eventLog);
    }

    @Override
    protected void validate(EventLog eventLog) {
        Optional<EventLog> event = eventLogService.getLastEventByUserId(eventLog.getUserId());
        if (lastEventWasDelete(event))
            throw new IllegalArgumentException("Cannot Register an already registered user.");
    }

    private boolean lastEventWasDelete(Optional<EventLog> event) {
        return event.isPresent() && event.get().getType() != EventType.DELETED;
    }
}


