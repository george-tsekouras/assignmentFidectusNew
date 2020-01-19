package com.fidectus.eventlog.rest;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.dto.EventLogDto;
import com.fidectus.eventlog.service.EventTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/event")
public class EventLogControllerImpl implements EventLogController {

    @Autowired
    private EventTypeFactory eventTypeFactory;

    @Override
    @PostMapping
    public ResponseEntity<EventLogDto> saveEventLog(@Valid EventLogDto eventLogDto) {
        EventLog event = eventLogFromDto(eventLogDto);
        event = eventTypeFactory.getTypeService(event.getType()).save(event);
        return ResponseEntity.ok(eventLogToDto(event));
    }

    private EventLogDto eventLogToDto(EventLog eventLog) {
        return new EventLogDto(
            eventLog.getType().toString(),
            eventLog.getUserId(),
            eventLog.getCreatedDate(),
            eventLog.getHash());
    }

    private EventLog eventLogFromDto(EventLogDto eventLogDto) {
        return new EventLog(
                EventType.valueOf(eventLogDto.getType()),
                eventLogDto.getUserId(),
                eventLogDto.getCreatedDate(),
                eventLogDto.getHash()
        );
    }

}
