package com.fidectus.eventlog.rest.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidectus.eventlog.domain.EventLog;
import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.dto.EventLogDto;
import com.fidectus.eventlog.rest.EventLogControllerImpl;
import com.fidectus.eventlog.service.EventLogService;
import com.fidectus.eventlog.service.EventTypeFactory;
import com.fidectus.eventlog.service.impl.EventRegisterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(EventLogControllerImpl.class)
public class EventLogControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventLogService eventLogService;

    @MockBean
    private EventTypeFactory eventTypeFactory;

    @MockBean
    private EventRegisterService eventRegisterService;

    @MockBean
    private JpaMetamodelMappingContext jpaMappingContext;

    private EventLog savedEvent;
    private EventLog eventFromDto;
    private EventLogDto eventLogDto;

    @Before
    public void setUp() {
        savedEvent = new EventLog(EventType.REGISTRATION,25L, LocalDateTime.now(),"this is the hash");
        savedEvent.setId(1L);

        eventLogDto = new EventLogDto(savedEvent.getType().toString(), savedEvent.getUserId(), null, null);

        eventFromDto = new EventLog();
        eventFromDto.setType(EventType.REGISTRATION);
        eventFromDto.setUserId(eventLogDto.getUserId());
    }

    @Test
    public void saveEventLog_success() throws Exception {
        when(eventTypeFactory.getTypeService(eventFromDto.getType())).thenReturn(eventRegisterService);
        when(eventRegisterService.save(Mockito.any())).thenReturn(savedEvent);
        mvc.perform(post("/event").contentType(MediaType.APPLICATION_JSON).content(asJsonString(eventLogDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is(savedEvent.getType().toString())));
        verify(eventTypeFactory, VerificationModeFactory.times(1)).getTypeService(eventFromDto.getType());
        verify(eventRegisterService, VerificationModeFactory.times(1)).save(Mockito.any());
        reset(eventTypeFactory);
        reset(eventRegisterService);
    }

    @Test
    public void saveEventLog_userId_shouldNotBeNull() throws Exception {
        eventLogDto.setUserId(null);
        mvc.perform(post("/event").contentType(MediaType.APPLICATION_JSON).content(asJsonString(eventLogDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveEventLog_eventType_shouldNotBeNull() throws Exception {
        eventLogDto.setType(null);
        mvc.perform(post("/event").contentType(MediaType.APPLICATION_JSON).content(asJsonString(eventLogDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveEventLog_createdDate_shouldBeNull() throws Exception {
        eventLogDto.setCreatedDate(LocalDateTime.now());
        mvc.perform(post("/event").contentType(MediaType.APPLICATION_JSON).content(asJsonString(eventLogDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveEventLog_hash_shouldBeNull() throws Exception {
        eventLogDto.setHash("this is the hash");
        mvc.perform(post("/event").contentType(MediaType.APPLICATION_JSON).content(asJsonString(eventLogDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveEventLog_serviceThrowIllegalArgumentException() throws Exception {
        when(eventTypeFactory.getTypeService(eventFromDto.getType())).thenReturn(eventRegisterService);
        when(eventRegisterService.save(Mockito.any())).thenThrow(new IllegalArgumentException());
        mvc.perform(post("/event").contentType(MediaType.APPLICATION_JSON).content(asJsonString(eventLogDto)))
                .andExpect(status().isBadRequest());
        verify(eventTypeFactory, VerificationModeFactory.times(1)).getTypeService(eventFromDto.getType());
        verify(eventRegisterService, VerificationModeFactory.times(1)).save(Mockito.any());
        reset(eventLogService);
        reset(eventRegisterService);
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
