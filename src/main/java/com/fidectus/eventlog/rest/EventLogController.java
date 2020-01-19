package com.fidectus.eventlog.rest;

import com.fidectus.eventlog.config.LogExecutionTime;
import com.fidectus.eventlog.dto.EventLogDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

public interface EventLogController {
    @ApiOperation(value = "API Save the EventLog which need the \"userId\" and the \"type\" of the event", response = EventLogDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully saved"),
            @ApiResponse(code = 400, message = "The data you are trying to save on \"userId\" and \"type\" are null or incorrect."),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 409, message = "Unique Value Violation Error")
    })
    @LogExecutionTime
    ResponseEntity<EventLogDto> saveEventLog(
            @ApiParam(value = "Save EventLogDto object with valid values. \n " +
                    "\"userId\" must be positive and greater than 0 (min(1))\n " +
                    "and the type that exists on EventLog.Enum")
            EventLogDto eventLogDto);

    @ApiOperation(value = "API returns a list with all events of the user or an empty list", response = EventLogDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieve user events"),
            @ApiResponse(code = 400, message = "If User does not exists."),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 409, message = "Unique Value Violation Error")
    })
    @LogExecutionTime
    ResponseEntity<List<EventLogDto>> getUserLogsById(
            @ApiParam(value = "The id of the User")
            long id);
}
