package com.fidectus.eventlog.service;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.dto.EventLogDto;
import com.fidectus.eventlog.repository.EventLogRepository;
import com.fidectus.eventlog.service.impl.EventLogServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventLogServiceTest {

    @InjectMocks
    private EventLogServiceImpl eventLogService;

    @Mock
    private EventLogRepository eventLogRepository;


    private EventLog event;
    private EventLogDto eventLogDto;

    @Before
    public void setUp() {

        event = new EventLog(EventType.REGISTRATION, 25L, LocalDateTime.now(), "this is the hash");
        event.setId(1L);

        eventLogDto = new EventLogDto(event.getType().toString(), event.getUserId(), null, null);
    }

    @Test
    public void getEventByUserId_found() {
        when(eventLogRepository.findAllByUserIdOrderByIdDesc(eventLogDto.getUserId())).thenReturn(List.of(event, event, event));
        List<EventLog> list = eventLogService.getEventByUserIdOrderById(event.getUserId());
        assertEquals(3, list.size());
        assertEquals(eventLogDto.getType(), list.get(0).getType().toString());
        list.forEach(event -> assertEquals(eventLogDto.getUserId(), event.getUserId()));
    }

    @Test
    public void getEventByUserId_notFound() {
        when(eventLogRepository.findAllByUserIdOrderByIdDesc(eventLogDto.getUserId())).thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> eventLogService.getEventByUserIdOrderById(event.getUserId()));
    }

    @Test
    public void hashTest_success() {
        String previousHash = event.getHash();
        eventLogService.hash(event);
        assertNotEquals(previousHash, event.getHash());
    }

    @Test
    public void hashTest_withNullUserId_shouldThrowNullPointerException() {
        event.setUserId(null);
        assertThrows(NullPointerException.class, () -> eventLogService.hash(event));
    }

    @Test
    public void hashTest_withNullCreateDate_shouldThrowNullPointerException() {
        event.setCreatedDate(null);
        assertThrows(NullPointerException.class, () -> eventLogService.hash(event));
    }

    @Test
    public void hashTest_withNullEventType_shouldThrowNullPointerException() {
        event.setType(null);
        assertThrows(NullPointerException.class, () -> eventLogService.hash(event));
    }

    @Test
    public void getLastEventByUserId_found() {
        when(eventLogService.getLastEventByUserId(event.getUserId())).thenReturn(Optional.of(event));
        assertTrue(eventLogService.getLastEventByUserId(event.getUserId()).isPresent());
    }

    @Test
    public void getLastEventByUserId_notFound() {
        when(eventLogService.getLastEventByUserId(event.getUserId())).thenReturn(Optional.empty());
        assertFalse(eventLogService.getLastEventByUserId(event.getUserId()).isPresent());
    }
}