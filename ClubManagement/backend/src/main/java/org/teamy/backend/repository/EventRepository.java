package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.VenueDataMapper;
import org.teamy.backend.model.Event;

import java.sql.SQLException;
import java.util.List;

public class EventRepository {
    private final EventDataMapper eventDataMapper;
    private final VenueDataMapper venueDataMapper;
    private static EventRepository instance;


    private EventRepository(EventDataMapper eventDataMapper, VenueDataMapper venueDataMapper) {
        this.eventDataMapper = eventDataMapper;
        this.venueDataMapper = venueDataMapper;
    }
    public static synchronized EventRepository getInstance(EventDataMapper eventDataMapper, VenueDataMapper venueDataMapper){
        if (instance == null){
            instance = new EventRepository(eventDataMapper, venueDataMapper);
        }
        return instance;
    }
    public Event findEventById(int Id) {
        Event event =  eventDataMapper.findEventById(Id);
        event.setVenueName(venueDataMapper.findVenueById(event.getVenueId()).getName());
        return event;
    }
    public void deleteEvent(int eventId) {
        eventDataMapper.deleteEvent(eventId);
    }
    public List<Event> findEventsByTitle(String title) throws SQLException {
        return eventDataMapper.findEventsByTitle(title);
    }
    public boolean saveEvent(Event event) throws Exception {
        return eventDataMapper.saveEvent(event);
    }
    public List<Event> getAllEvent() {
        return eventDataMapper.getAllEvent();
    }
    public boolean updateEvent(Event event) throws Exception {
        return eventDataMapper.updateEvent(event);
    }
}
