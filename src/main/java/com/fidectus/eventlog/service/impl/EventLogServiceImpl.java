package com.fidectus.eventlog.service.impl;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.repository.EventLogRepository;
import com.fidectus.eventlog.service.EventLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;


@Service
public class EventLogServiceImpl implements EventLogService {

    private final EventLogRepository eventLogRepository;

    public EventLogServiceImpl(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    @Override
    public List<EventLog> getEventByUserIdOrderById(long userId) {
        List<EventLog> list = eventLogRepository.findAllByUserIdOrderByIdDesc(userId);
        if (list.isEmpty())
            throw new IllegalArgumentException(String.format("User with id '%d' does not exists.", userId));
        return list;
    }

    @Override
    public void hash(EventLog event) {
        if (event.getUserId() == null || event.getType() == null || event.getCreatedDate() == null)
            throw new NullPointerException("UserId, EventType and CreateDate should not be null");
        String source = "" + event.getUserId() + event.getCreatedDate() + event.getType() ;
        byte[] bytes = source.getBytes(UTF_8);
        UUID uuid = UUID.nameUUIDFromBytes(bytes);
        event.setHash(uuid.toString());
    }

    @Override
    public Optional<EventLog> getLastEventByUserId(Long userId) {
        return eventLogRepository.findFirstByUserIdOrderByIdDesc(userId);
    }

}
