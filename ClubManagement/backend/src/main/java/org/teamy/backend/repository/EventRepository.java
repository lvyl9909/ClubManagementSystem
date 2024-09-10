package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.model.Event;

public class EventRepository {
    private final EventDataMapper eventDataMapper;

    public EventRepository(EventDataMapper eventDataMapper) {
        this.eventDataMapper = eventDataMapper;
    }
    public Event findEventById(int Id) {
        return eventDataMapper.findEventById(Id);
    }

}
