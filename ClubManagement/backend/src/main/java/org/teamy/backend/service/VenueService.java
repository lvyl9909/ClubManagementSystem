package org.teamy.backend.service;

import org.teamy.backend.DataMapper.VenueDataMapper;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.Venue;

import java.util.List;

public class VenueService {
    private final VenueDataMapper venueDataMapper;

//    private static VenueService instance;
//    private final VenueDataMapper venueDataMapper;
//
//    // 私有构造函数，防止外部直接创建实例
//    private VenueService(DatabaseConnectionManager databaseConnectionManager) {
//        this.venueDataMapper = new VenueDataMapper(databaseConnectionManager);
//    }
//
//    // 提供全局访问点
//    public static synchronized VenueService getInstance(DatabaseConnectionManager databaseConnectionManager) {
//        if (instance == null) {
//            instance = new VenueService(databaseConnectionManager);
//        }
//        return instance;
//    }
    public VenueService(VenueDataMapper venueDataMapper) {
        this.venueDataMapper = venueDataMapper;
    }
    public Venue getVenueById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Venue venue = null;
        try {
            venue = venueDataMapper.findVenueById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return venue;
    }

    public List<Venue> getAllVenue()throws Exception{
        List<Venue> venues = null;
        try {
            venues = venueDataMapper.getAllVenue();
            return venues;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
