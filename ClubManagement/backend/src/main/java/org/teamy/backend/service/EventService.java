package org.teamy.backend.service;

import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;

import java.util.Collections;
import java.util.List;

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

    public boolean saveEvent(Event event) throws Exception {
        // 可以在这里添加额外的业务逻辑，比如数据验证
        if (event ==null||event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Club cannot be empty");
        }

        // 调用DAO层的方法
        return eventDataMapper.saveEvent(event);
    }

    public List<Event> getAllEvents() {
        try {
            return eventDataMapper.getAllEvent();
        } catch (Exception e) {
            // 在这里处理异常，例如记录日志或抛出自定义异常
            System.err.println("Error occurred while fetching clubs: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // 返回一个空列表以防止上层代码崩溃
        }
    }
}
