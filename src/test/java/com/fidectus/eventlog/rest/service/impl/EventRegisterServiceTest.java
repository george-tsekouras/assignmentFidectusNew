package com.fidectus.eventlog.rest.service.impl;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.repository.EventLogRepository;
import com.fidectus.eventlog.service.EventLogService;
import com.fidectus.eventlog.service.impl.EventRegisterService;
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
public class EventRegisterServiceTest {

    @InjectMocks
    private EventRegisterService eventRegisterService;

    @Mock
    private EventLogService eventLogService;

    @Mock
    private EventLogRepository eventLogRepository;

    private EventLog eventLog;
    private EventLog dbEventLog;

    @Before
    public void setUp() {
        eventLog = new EventLog(EventType.REGISTRATION, 25L, null, null);
        dbEventLog = new EventLog(EventType.DELETED, 25L, null, null);

        when(eventLogService.getLastEventByUserId(eventLog.getUserId())).thenReturn(Optional.of(dbEventLog));
        when(eventLogService.getLastEventByUserId(0L)).thenReturn(Optional.empty());
    }

    @Test
    public void save_userDontExists() {
        eventLog.setUserId(0L);
        eventRegisterService.save(eventLog);
        verify(eventLogService, VerificationModeFactory.times(1)).getLastEventByUserId(eventLog.getUserId());
        verify(eventLogRepository, VerificationModeFactory.times(1)).save(eventLog);
    }

    @Test
    public void save_userExists_withLastEventDeleted() {
        dbEventLog.setType(EventType.DELETED);
        eventRegisterService.save(eventLog);
        verify(eventLogService, VerificationModeFactory.times(1)).getLastEventByUserId(eventLog.getUserId());
        verify(eventLogRepository, VerificationModeFactory.times(1)).save(eventLog);
    }

    @Test
    public void validate_userFountWithRegistration_shouldThrowIllegalArgumentException() {
        dbEventLog.setType(EventType.REGISTRATION);
        assertThrows(IllegalArgumentException.class,() -> eventRegisterService.save(eventLog));
    }

    @Test
    public void validate_userFountWithUpdateRegistration_shouldThrowIllegalArgumentException() {
        dbEventLog.setType(EventType.UPDATE_REGISTRATION);
        assertThrows(IllegalArgumentException.class,() -> eventRegisterService.save(eventLog));
    }

    @Test
    public void validate_userFountWithDeactivate_shouldThrowIllegalArgumentException() {
        dbEventLog.setType(EventType.DEACTIVATE);
        assertThrows(IllegalArgumentException.class,() -> eventRegisterService.save(eventLog));
    }

    @Test
    public void validate() {
        eventRegisterService.save(eventLog);
        verify(eventLogService, VerificationModeFactory.times(1)).getLastEventByUserId(eventLog.getUserId());
    }
}