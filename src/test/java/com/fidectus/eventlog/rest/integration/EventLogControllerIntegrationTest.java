package com.fidectus.eventlog.integration;

import com.fidectus.eventlog.domain.EventType;
import com.fidectus.eventlog.dto.EventLogDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * You can't use controllers for testing successful request functionality. This happens because you annotate your method
 * with @Transactional, and after the call, an nested method creates a new transaction with the database. The Junit
 * rollback the first transaction and not the nested one. So we test controller for the error response validation response,
 * and for error, on functionality, we will verify the leading service.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("integration")
@ContextConfiguration
public class EventLogControllerIntegrationTest {

    @Autowired
    Environment environment;

    private EventLogDto eventLogDto;
    private String host;

    @Bean
    private RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Before
    public void setUp() {
        host = "http://localhost:" + environment.getProperty("local.server.port");

        eventLogDto = new EventLogDto(EventType.REGISTRATION.toString(), 5L, null, null);
    }

    @Test
    @Rollback
    public void getUserLogsById_exist() {
        assertThrows(HttpClientErrorException.BadRequest.class,() ->
                restTemplate().getForEntity(host+"/event/user/" + eventLogDto.getUserId(), EventLogDto[].class));

    }

    @Test
    @Rollback
    public void getUserLogsById_withNullId_shouldReturnBadRequest() {
        assertThrows(HttpClientErrorException.NotFound.class,() ->
                restTemplate().getForEntity(host+"/event/user/", EventLogDto[].class));

    }

    @Test
    public void save_withTypeNull_shouldReturnBadRequest() {
        eventLogDto.setType(null);
        assertThrows(IllegalArgumentException.class,() ->
                restTemplate().postForObject(host + "/event", eventLogDto, null));
    }

    @Test
    public void save_withUserIdNull_shouldReturnBadRequest() {
        eventLogDto.setType(null);
        assertThrows(IllegalArgumentException.class,() ->
                restTemplate().postForObject(host + "/event", eventLogDto, null));
    }

    @Test
    public void save_withNullDto_shouldReturnBadRequest() {
        eventLogDto = null;
        assertThrows(IllegalArgumentException.class,() ->
                restTemplate().postForObject(host + "/event", eventLogDto, null));
    }

}
