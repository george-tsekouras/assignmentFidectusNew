package com.fidectus.eventlog.rest.service.impl;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.repository.EventLogRepository;
import com.fidectus.eventlog.service.EventLogService;
import com.fidectus.eventlog.service.impl.EventDeactivateService;
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
public class EventDeactivateServiceTest {

    @InjectMocks
    private EventDeactivateService eventDeactivateService;

    @Mock
    private EventLogService eventLogService;

    @Mock
    private EventLogRepository eventLogRepository;

    private EventLog eventLog;
    private EventLog dbEventLog;

    @Before
    public void setUp() {
        eventLog = new EventLog(EventType.DEACTIVATE, 25L, null, null);
        dbEventLog = new EventLog(EventType.REGISTRATION, 25L, null, null);

        when(eventLogService.getLastEventByUserId(eventLog.getUserId())).thenReturn(Optional.of(dbEventLog));
        when(eventLogService.getLastEventByUserId(0L)).thenReturn(Optional.empty());
    }

    @Test
    public void save_userExists_withLastEventRegistration() {
        eventDeactivateService.save(eventLog);
        verify(eventLogService, VerificationModeFactory.times(1)).getLastEventByUserId(eventLog.getUserId());
        verify(eventLogRepository, VerificationModeFactory.times(1)).save(eventLog);
    }

    @Test
    public void save_userExists_withLastEventUpdateRegistration() {
        dbEventLog.setType(EventType.UPDATE_REGISTRATION);
        eventDeactivateService.save(eventLog);
        verify(eventLogService, VerificationModeFactory.times(1)).getLastEventByUserId(eventLog.getUserId());
        verify(eventLogRepository, VerificationModeFactory.times(1)).save(eventLog);
    }

    @Test
    public void validate_userNotExists_shouldThrowIllegalArgumentException() {
        eventLog.setUserId(0L);
        assertThrows(IllegalArgumentException.class,() -> eventDeactivateService.save(eventLog));
    }

    @Test
    public void validate_userLastEventDelete_shouldThrowIllegalArgumentException() {
        dbEventLog.setType(EventType.DELETED);
        assertThrows(IllegalArgumentException.class,() -> eventDeactivateService.save(eventLog));
    }
}