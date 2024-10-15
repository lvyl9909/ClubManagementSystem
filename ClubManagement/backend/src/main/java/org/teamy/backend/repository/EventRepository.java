package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.VenueDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.teamy.backend.model.FundingApplication;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventRepository {
    private final EventDataMapper eventDataMapper;
    private final VenueDataMapper venueDataMapper;
    private final ClubDataMapper clubDataMapper;

    private static EventRepository instance;

    private EventRepository(EventDataMapper eventDataMapper, VenueDataMapper venueDataMapper, ClubDataMapper clubDataMapper) {
        this.eventDataMapper = eventDataMapper;
        this.venueDataMapper = venueDataMapper;
        this.clubDataMapper = clubDataMapper;
    }
    public static synchronized EventRepository getInstance(EventDataMapper eventDataMapper, VenueDataMapper venueDataMapper, ClubDataMapper clubDataMapper){
        if (instance == null){
            instance = new EventRepository(eventDataMapper, venueDataMapper,clubDataMapper);
        }
        return instance;
    }
    // 查找事件时先检查缓存
    public Event findEventById(int id,Connection connection) {
        // 先从缓存中获取
        Event event;
        event = eventDataMapper.findEventById(id,connection);
        event.setVenue(venueDataMapper.findVenueById(event.getVenueId(),connection));
        event.setClub(clubDataMapper.findClubById(event.getVenueId(),connection));
        if (event != null) {
            event.setVenueName(venueDataMapper.findVenueById(event.getVenueId(),connection).getName());
        }
        return event;
    }
    // Delete the event and remove it from the cache
    public void deleteEvent(Connection connection,int eventId) {
        eventDataMapper.deleteEvent(connection,eventId);
        // 从缓存中移除对应的事件
    }    public List<Event> findEventsByTitle(String title) throws SQLException {
        return eventDataMapper.findEventsByTitle(title);
    }
    // Save the event and update the cache
    public boolean saveEvent(Event event) throws Exception {
        boolean result = eventDataMapper.saveEvent(event);
        if (result) {
            // 更新缓存
//            eventCache.put(event.getId(), event);
        }
        return result;
    }
    public List<Event> getAllEvent() {
        return eventDataMapper.getAllEvent();
    }

    // 更新事件并更新缓存
    public boolean updateEvent(Event event,Connection connection) throws Exception {
        boolean result = eventDataMapper.updateEvent(event,connection);
        return result;
    }

    public Event lazyLoadClub(Event event,Connection connection){

        try {
            Club club = clubDataMapper.findClubById(event.getClubId(),connection);
            System.out.println(club);
            event.setClub(club);
//            eventCache.put(event.getId(),event);
            return event;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateCapacity(Event event,Connection connection) throws Exception {
        boolean result = eventDataMapper.updateEventCapacity(event,connection);
        return result;
    }
}
