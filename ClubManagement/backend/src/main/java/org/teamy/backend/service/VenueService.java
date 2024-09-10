package org.teamy.backend.service;

import org.teamy.backend.model.Venue;
import org.teamy.backend.repository.VenueRepository;

import java.util.List;

public class VenueService {
    private final VenueRepository venueRepository;
    private static VenueService instance;
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
    private VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }
    public static synchronized VenueService getInstance(VenueRepository venueRepository){
        if(instance == null){
            instance = new VenueService(venueRepository);
        }
        return instance;
    }


    public List<Venue> getAllVenue()throws Exception{
        List<Venue> venues = null;
        try {
            venues = venueRepository.getAllVenue();
            return venues;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
