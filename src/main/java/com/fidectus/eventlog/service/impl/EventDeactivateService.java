package com.fidectus.eventlog.service.impl;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.service.EventLogService;
import com.fidectus.eventlog.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EventDeactivateService extends EventTypeService {

    @Autowired
    private EventLogService eventLogService;

    @Override
    public EventType getType() {
        return EventType.DEACTIVATE;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public EventLog save(EventLog eventLog) {
        validate(eventLog);
        return saveEvent(eventLog);
    }

    @Override
    protected void validate(EventLog eventLog) {
        EventLog event = eventLogService.getLastEventByUserId(eventLog.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("You cannot deactivate an unregister user"));
        if (event.getType() == EventType.DELETED || event.getType() == EventType.DEACTIVATE)
            throw new IllegalArgumentException("You cannot deactivate a deleted user");
    }
}
