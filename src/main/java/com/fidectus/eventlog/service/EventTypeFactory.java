package com.fidectus.eventlog.service;

import com.fidectus.eventlog.domain.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.EnumMap;
import java.util.List;

@Service
public class EventTypeFactory {

    EnumMap<EventType, EventTypeService> servicesByTypes;

    @Autowired
    public EventTypeFactory(List<EventTypeService> eventTypeServices) {
        servicesByTypes = new EnumMap<>(EventType.class);
        eventTypeServices.forEach(typeService -> servicesByTypes.put(typeService.getType(), typeService));
    }

    public EventTypeService getTypeService(@NotNull EventType eventType) {
        EventTypeService service = servicesByTypes.get(eventType);
        if (service == null)
            throw new IllegalStateException("Didn't find Service with type: " + eventType);
        else
            return service;
    }

}
