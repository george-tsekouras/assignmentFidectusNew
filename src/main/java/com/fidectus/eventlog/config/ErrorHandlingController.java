package com.fidectus.eventlog.config;

import com.fidectus.eventlog.dto.ApiErrorDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class ErrorHandlingController extends ResponseEntityExceptionHandler {

    private static final ExceptionMapping DEFAULT_ERROR = new ExceptionMapping(
            "SERVER_ERROR", INTERNAL_SERVER_ERROR);

    private static final ExceptionMapping GENERAL_ERROR = new ExceptionMapping("GENERAL_ERROR", BAD_REQUEST);

    @SuppressWarnings("rawtypes")
    private final Map<Class, ExceptionMapping> exceptionMappings = new HashMap<>();

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiErrorDto> handleThrowable(final Throwable ex, final HttpServletResponse response) {
        ExceptionMapping mapping = exceptionMappings.getOrDefault(ex.getClass(), GENERAL_ERROR);
        if (response.getStatus() >= 500) {
            mapping = exceptionMappings.getOrDefault(ex.getClass(), DEFAULT_ERROR);
        }
        if(ex.getMessage() != null) {
            mapping.message = ex.getMessage();
        }
        log.error(ex.getMessage(), ex);
        ApiErrorDto error = new ApiErrorDto(mapping.status, mapping.message);
        return new ResponseEntity(error, error.getStatus());
    }

    public ErrorHandlingController() {
        exceptionMapping(IllegalArgumentException.class, "Illegal Argument Error", BAD_REQUEST);
        exceptionMapping(IllegalStateException.class, "Illegal State Error", BAD_REQUEST);
        exceptionMapping(DataIntegrityViolationException.class, "Unique Value Violation Error", CONFLICT);
        exceptionMapping(RuntimeException.class, "Run Time Exception", BAD_REQUEST);
        exceptionMapping(MissingServletRequestParameterException.class, "MISSING_PARAMETER", BAD_REQUEST);
        exceptionMapping(HttpRequestMethodNotSupportedException.class, "METHOD_NOT_SUPPORTED", METHOD_NOT_ALLOWED);
        exceptionMapping(ServletRequestBindingException.class, "MISSING_HEADER", BAD_REQUEST);
    }

    @AllArgsConstructor
    private static class ExceptionMapping {
        private String message;
        private final HttpStatus status;
    }

    private void exceptionMapping(
            final Class<?> theClass,
            final String message,
            final HttpStatus status) {
        exceptionMappings.put(theClass, new ExceptionMapping(message, status));
    }
}
