package org.teamy.backend.service;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;

public class EventService {
    EventDataMapper eventDataMapper;

    public EventService(EventDataMapper eventDataMapper) {
        this.eventDataMapper = eventDataMapper;
    }
    public Event getEventById(Integer id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Event event = eventDataMapper.findEventById(id);
//        if (club == null) {
//            throw new Exception("Club with name '" + title + "' not found");
//        }
        return event;
    }
    public Event getEventByTitle(String title) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be null or empty");
        }

        Event event = eventDataMapper.findEventByTitle(title);
//        if (club == null) {
//            throw new Exception("Club with name '" + title + "' not found");
//        }

        return event;
    }
}
