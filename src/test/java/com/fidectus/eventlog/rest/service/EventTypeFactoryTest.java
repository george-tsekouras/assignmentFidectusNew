package com.fidectus.eventlog.rest.service;

import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.service.EventTypeFactory;
import com.fidectus.eventlog.service.EventTypeService;
import com.fidectus.eventlog.service.impl.EventDeactivateService;
import com.fidectus.eventlog.service.impl.EventDeleteService;
import com.fidectus.eventlog.service.impl.EventRegisterService;
import com.fidectus.eventlog.service.impl.EventUpdateRegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventTypeFactoryTest {

    @Mock
    private EventRegisterService eventRegisterService;
    @Mock
    private EventDeleteService eventDeleteService;
    @Mock
    private EventDeactivateService eventDeactivateService;
    @Mock
    private EventUpdateRegistrationService eventUpdateRegistrationService;

    private EventTypeFactory eventTypeFactory;

    @Before
    public void setUp() {
        when(eventDeactivateService.getType()).thenReturn(EventType.DEACTIVATE);
        when(eventDeleteService.getType()).thenReturn(EventType.DELETED);
        when(eventRegisterService.getType()).thenReturn(EventType.REGISTRATION);
        when(eventUpdateRegistrationService.getType()).thenReturn(EventType.UPDATE_REGISTRATION);

        eventTypeFactory = new EventTypeFactory(
            List.of(eventDeactivateService, eventDeleteService,eventRegisterService,eventUpdateRegistrationService));
    }

    @Test
    public void eventTypeFactory_constructor() {
        EventTypeService registrationService = eventTypeFactory.getTypeService(EventType.REGISTRATION);
        assertNotNull(registrationService);
        EventTypeService deletedService = eventTypeFactory.getTypeService(EventType.REGISTRATION);
        assertNotNull(deletedService);
        EventTypeService deactivateService = eventTypeFactory.getTypeService(EventType.REGISTRATION);
        assertNotNull(deactivateService);
        EventTypeService updateService = eventTypeFactory.getTypeService(EventType.REGISTRATION);
        assertNotNull(updateService);
    }

    @Test
    public void eventTypeFactory_constructor_shouldThrowNullPointerException() {
        when(eventRegisterService.getType()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> new EventTypeFactory(
                List.of(eventDeactivateService, eventDeleteService,eventRegisterService,eventUpdateRegistrationService)));
    }

    @Test
    public void getTypeService_registration() {
        EventTypeService registrationService = eventTypeFactory.getTypeService(EventType.REGISTRATION);
        assertNotNull(registrationService);
        assertEquals(eventRegisterService.getClass(), registrationService.getClass());
        assertEquals(eventRegisterService.getType(), registrationService.getType());
    }


    @Test
    public void getTypeService_deleted() {
        EventTypeService deletedService = eventTypeFactory.getTypeService(EventType.DELETED);
        assertNotNull(deletedService);
        assertEquals(eventDeleteService.getClass(), deletedService.getClass());
        assertEquals(eventDeleteService.getType(), deletedService.getType());
    }


    @Test
    public void getTypeService_updateRegistration() {
        EventTypeService updateService = eventTypeFactory.getTypeService(EventType.UPDATE_REGISTRATION);
        assertNotNull(updateService);
        assertEquals(eventUpdateRegistrationService.getClass(), updateService.getClass());
        assertEquals(eventUpdateRegistrationService.getType(), updateService.getType());
    }


    @Test
    public void getTypeService_deactivate() {
        EventTypeService deactivateService = eventTypeFactory.getTypeService(EventType.DEACTIVATE);
        assertNotNull(deactivateService);
        assertEquals(eventDeactivateService.getClass(), deactivateService.getClass());
        assertEquals(eventDeactivateService.getType(), deactivateService.getType());
    }

    @Test
    public void getTypeService_shouldThrowIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> eventTypeFactory.getTypeService(null));
    }
}
