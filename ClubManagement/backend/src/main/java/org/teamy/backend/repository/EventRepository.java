package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.DataMapper.VenueDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.teamy.backend.model.FundingApplication;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventRepository {
    private final EventDataMapper eventDataMapper;
    private final VenueDataMapper venueDataMapper;
    private final ClubDataMapper clubDataMapper;

    private static EventRepository instance;
    private final Cache<Integer, Event> eventCache;


    private EventRepository(EventDataMapper eventDataMapper, VenueDataMapper venueDataMapper, ClubDataMapper clubDataMapper) {
        this.eventDataMapper = eventDataMapper;
        this.venueDataMapper = venueDataMapper;
        this.clubDataMapper = clubDataMapper;

        // 初始化缓存，设置最大容量和过期时间
        this.eventCache = CacheBuilder.newBuilder()
                .maximumSize(100) // 最大缓存100个事件
                .expireAfterWrite(30, TimeUnit.MINUTES) // 缓存条目在10分钟后过期
                .build();
    }
    public static synchronized EventRepository getInstance(EventDataMapper eventDataMapper, VenueDataMapper venueDataMapper, ClubDataMapper clubDataMapper){
        if (instance == null){
            instance = new EventRepository(eventDataMapper, venueDataMapper,clubDataMapper);
        }
        return instance;
    }
    // 查找事件时先检查缓存
    public Event findEventById(int id) {
        // 先从缓存中获取
        Event event = eventCache.getIfPresent(id);
        if (event != null) {
            return event; // 如果缓存中有，则直接返回
        }

        // 如果缓存中没有，查询数据库
        event = eventDataMapper.findEventById(id);
        event.setVenue(venueDataMapper.findVenueById(event.getVenueId()));
        event.setClub(clubDataMapper.findClubById(event.getVenueId()));
        if (event != null) {
            event.setVenueName(venueDataMapper.findVenueById(event.getVenueId()).getName());

            // 将查询结果存入缓存
            eventCache.put(id, event);
        }
        return event;
    }
    // 删除事件并从缓存中移除
    public void deleteEvent(int eventId) {
        eventDataMapper.deleteEvent(eventId);
        // 从缓存中移除对应的事件
        eventCache.invalidate(eventId);
    }    public List<Event> findEventsByTitle(String title) throws SQLException {
        return eventDataMapper.findEventsByTitle(title);
    }
    // 保存事件并更新缓存
    public boolean saveEvent(Event event) throws Exception {
        boolean result = eventDataMapper.saveEvent(event);
        if (result) {
            // 更新缓存
            eventCache.put(event.getId(), event);
        }
        return result;
    }
    public List<Event> getAllEvent() {
        return eventDataMapper.getAllEvent();
    }

    // 更新事件并更新缓存
    public boolean updateEvent(Event event) throws Exception {
        boolean result = eventDataMapper.updateEvent(event);
        if (result) {
            // 更新缓存
            eventCache.put(event.getId(), event);
        }
        return result;
    }

    public Event lazyLoadClub(Event event){

        Club club = clubDataMapper.findClubById(event.getClubId());
        event.setClub(club);
        eventCache.put(event.getId(),event);

        return event;
    }
}
