package com.fidectus.eventlog.rest.service.impl;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.repository.EventLogRepository;
import com.fidectus.eventlog.service.EventLogService;
import com.fidectus.eventlog.service.impl.EventDeleteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventDeleteServiceTest {

    @InjectMocks
    private EventDeleteService eventDeleteService;

    @Mock
    private EventLogService eventLogService;

    @Mock
    private EventLogRepository eventLogRepository;

    private EventLog eventLog;

    @Before
    public void setUp() {
        eventLog = new EventLog(EventType.REGISTRATION, 25L, null, null);

        when(eventLogService.getLastEventByUserId(eventLog.getUserId())).thenReturn(Optional.of(eventLog));
        when(eventLogService.getLastEventByUserId(0L)).thenReturn(Optional.empty());
    }

    @Test
    public void save() {
        eventDeleteService.save(eventLog);
        verify(eventLogService, VerificationModeFactory.times(1)).getLastEventByUserId(eventLog.getUserId());
        verify(eventLogRepository, VerificationModeFactory.times(1)).save(eventLog);
    }

    @Test
    public void validate_userNotFount_shouldThrowIllegalArgumentException() {
        eventLog.setUserId(0L);
        assertThrows(IllegalArgumentException.class,() -> eventDeleteService.save(eventLog));
    }

    @Test
    public void validate_lastEventTypeDelete_shouldThrowIllegalArgumentException() {
        eventLog.setType(EventType.DELETED);
        assertThrows(IllegalArgumentException.class,() -> eventDeleteService.save(eventLog));
    }

    @Test
    public void validate() {
        eventDeleteService.save(eventLog);
        verify(eventLogService, VerificationModeFactory.times(1)).getLastEventByUserId(eventLog.getUserId());
    }
}