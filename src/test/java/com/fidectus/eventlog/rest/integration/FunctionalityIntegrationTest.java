package com.fidectus.eventlog.rest.integration;

import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.service.EventLogService;
import com.fidectus.eventlog.service.EventTypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("integration")
@ContextConfiguration
@Transactional
public class FunctionalityIntegrationTest {

    @Autowired
    private EventTypeFactory eventTypeFactory;

    @Autowired
    private EventLogService eventLogService;

    private EventLog firstEvent;
    private EventLog previousEvent;
    private EventLog testEvent;

    @Before
    public void setUp() {
        firstEvent = new EventLog(EventType.REGISTRATION, 5L, null, null);
        previousEvent = new EventLog(EventType.REGISTRATION, 5L, null, null);
        testEvent = new EventLog(EventType.REGISTRATION, 5L, null, null);
    }

    // ============================== EventLogService hash test ==============================
    @Test
    public void hash_withNullCreateDate_shouldThrowNullPointerException() {
        firstEvent.setId(1L);
        firstEvent.setCreatedDate(null);
        assertThrows(NullPointerException.class, () ->
                eventLogService.hash(firstEvent));
    }

    @Test
    public void hash_withNullId_shouldThrowNullPointerException() {
        firstEvent.setUserId(null);
        firstEvent.setCreatedDate(LocalDateTime.now());
        assertThrows(NullPointerException.class, () ->
                eventLogService.hash(firstEvent));
    }

    @Test
    public void hash_withNullType_shouldThrowNullPointerException() {
        firstEvent.setId(1L);
        firstEvent.setCreatedDate(LocalDateTime.now());
        firstEvent.setType(null);
        assertThrows(NullPointerException.class, () ->
                eventLogService.hash(firstEvent));
    }

    @Test
    public void hash_successful() {
        firstEvent.setCreatedDate(LocalDateTime.now());
        firstEvent.setId(1L);
        assertDoesNotThrow(() ->eventLogService.hash(firstEvent));
    }

    // ============================== EventLogService getLastEventByUserId test ==============================
    @Test
    public void getLastEventByUserId_userDontExist_shouldReturnOptionalEmpty() {
        Optional<EventLog> optional = eventLogService.getLastEventByUserId(2L);
        assertFalse(optional.isPresent());
    }

    @Test
    public void getLastEventByUserId_userExist_shouldReturnLastEvent_1() {
        setUpUsersHistory(EventType.REGISTRATION); //with this we create only one Event
        Optional<EventLog> optional = eventLogService.getLastEventByUserId(firstEvent.getUserId());
        assertTrue(optional.isPresent());
        assertSavedEventLogIsValid(optional.get(), EventType.REGISTRATION);
    }

    @Test
    public void getLastEventByUserId_userExist_shouldReturnLastEvent_2() {
        setUpUsersHistory(EventType.UPDATE_REGISTRATION); //with this we create two Event
        Optional<EventLog> optional = eventLogService.getLastEventByUserId(firstEvent.getUserId());
        assertTrue(optional.isPresent());
        assertSavedEventLogIsValid(optional.get(), EventType.UPDATE_REGISTRATION);
    }

    // ============================== EventType REGISTRATION test ==============================
    @Test
    public void save_registration_userNotExist() {
        testEvent.setType(EventType.REGISTRATION);
        testEvent = eventTypeFactory.getTypeService(testEvent.getType()).save(testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.REGISTRATION);
    }

    @Test
    public void save_registration_previousEventDeleted() {
        setUpUsersHistory(EventType.DELETED);
        testEvent.setType(EventType.REGISTRATION);
        testEvent = eventTypeFactory.getTypeService(firstEvent.getType()).save(firstEvent);
        assertSavedEventLogIsValid(testEvent, EventType.REGISTRATION);
    }

    @Test
    public void save_registration_previousEventDeactivate_shouldThrowIllegalArgumentException() {
        setUpUsersHistory(EventType.DEACTIVATE);
        testEvent.setType(EventType.REGISTRATION);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent));
    }

    @Test
    public void save_registration_previousEventUpdateRegistration_shouldThrowIllegalArgumentException() {
        setUpUsersHistory(EventType.UPDATE_REGISTRATION);
        testEvent.setType(EventType.REGISTRATION);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent));
    }

    @Test
    public void save_registration_previousEventRegistration_shouldThrowIllegalArgumentException() {
        setUpUsersHistory(EventType.REGISTRATION);
        testEvent.setType(EventType.REGISTRATION);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent));
    }

    // ============================== EventType DELETE test ==============================
    @Test
    public void save_delete_userNotExist() {
        testEvent.setType(EventType.DELETED);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(testEvent.getType()).save(testEvent));
    }

    @Test
    public void save_delete_previousEventDeleted_shouldThrowIllegalArgumentException() {
        setUpUsersHistory(EventType.DELETED);
        testEvent.setType(EventType.DELETED);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent));
    }

    @Test
    public void save_delete_previousEventDeactivate() {
        setUpUsersHistory(EventType.DEACTIVATE);
        testEvent.setType(EventType.DELETED);
        testEvent = eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.DELETED);
    }

    @Test
    public void save_delete_previousEventUpdateRegistration() {
        setUpUsersHistory(EventType.UPDATE_REGISTRATION);
        testEvent.setType(EventType.DELETED);
        testEvent = eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.DELETED);
    }

    @Test
    public void save_delete_previousEventRegistration() {
        setUpUsersHistory(EventType.UPDATE_REGISTRATION);
        testEvent.setType(EventType.DELETED);
        testEvent = eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.DELETED);
    }

    // ============================== EventType DEACTIVATE test ==============================
    @Test
    public void save_deactivate_userNotExist() {
        testEvent.setType(EventType.DEACTIVATE);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(testEvent.getType()).save(testEvent));
    }

    @Test
    public void save_deactivate_previousEventDeleted_shouldThrowIllegalArgumentException() {
        setUpUsersHistory(EventType.DELETED);
        testEvent.setType(EventType.DEACTIVATE);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent));
    }

    @Test
    public void save_deactivate_previousEventDeactivate_shouldThrowIllegalArgumentException() {
        setUpUsersHistory(EventType.DEACTIVATE);
        testEvent.setType(EventType.DEACTIVATE);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent));
    }

    @Test
    public void save_deactivate_previousEventUpdateRegistration() {
        setUpUsersHistory(EventType.UPDATE_REGISTRATION);
        testEvent.setType(EventType.DEACTIVATE);
        testEvent = eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.DEACTIVATE);
    }

    @Test
    public void save_deactivate_previousEventRegistration() {
        setUpUsersHistory(EventType.UPDATE_REGISTRATION);
        testEvent.setType(EventType.DEACTIVATE);
        testEvent = eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.DEACTIVATE);
    }

    // ============================== EventType UPDATE_REGISTRATION test ==============================
    @Test
    public void save_updateRegistration_userNotExist_shouldThrowIllegalArgumentException() {
        testEvent.setType(EventType.UPDATE_REGISTRATION);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(testEvent.getType()).save(testEvent));
    }

    @Test
    public void save_updateRegistration_previousEventDeleted_shouldThrowIllegalArgumentException() {
        setUpUsersHistory(EventType.DELETED);
        testEvent.setType(EventType.UPDATE_REGISTRATION);
        assertThrows(IllegalArgumentException.class,() ->
                eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent));
    }

    @Test
    public void save_updateRegistration_previousEventDeactivate() {
        setUpUsersHistory(EventType.DEACTIVATE);
        testEvent.setType(EventType.UPDATE_REGISTRATION);
        testEvent = eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.UPDATE_REGISTRATION);
    }

    @Test
    public void save_updateRegistration_previousEventUpdateRegistration() {
        setUpUsersHistory(EventType.REGISTRATION);
        testEvent.setType(EventType.UPDATE_REGISTRATION);
        testEvent = eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.UPDATE_REGISTRATION);
    }

    @Test
    public void save_updateRegistration_previousEventRegistration() {
        setUpUsersHistory(EventType.UPDATE_REGISTRATION);
        testEvent.setType(EventType.UPDATE_REGISTRATION);
        testEvent = eventTypeFactory.getTypeService(this.testEvent.getType()).save(this.testEvent);
        assertSavedEventLogIsValid(testEvent, EventType.UPDATE_REGISTRATION);
    }

    private void setUpUsersHistory(EventType type) {
        if (type == EventType.REGISTRATION) {
            saveFirstEvent();
        } else {
            saveFirstEvent();
            saveSecondEvent(type);
        }
    }

    private void saveFirstEvent() {
        firstEvent.setType(EventType.REGISTRATION);
        firstEvent = eventTypeFactory.getTypeService(this.firstEvent.getType()).save(this.firstEvent);
        assertSavedEventLogIsValid(firstEvent, EventType.REGISTRATION);
    }

    private void saveSecondEvent(EventType type) {
        previousEvent.setType(type);
        previousEvent = eventTypeFactory.getTypeService(this.previousEvent.getType()).save(this.previousEvent);
        assertSavedEventLogIsValid(previousEvent, type);
    }

    private void assertSavedEventLogIsValid(EventLog eventLog, EventType type) {
        assertNotNull(eventLog);
        assertNotNull(eventLog.getHash());
        assertNotNull(eventLog.getCreatedDate());
        assertNotNull(eventLog.getId());
        assertEquals(type, eventLog.getType());

    }
}
