package org.teamy.backend.service;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Venue;
import org.teamy.backend.repository.VenueRepository;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class VenueService {
    private final VenueRepository venueRepository;
    private static VenueService instance;

    private final DatabaseConnectionManager databaseConnectionManager;
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
    private VenueService(VenueRepository venueRepository,DatabaseConnectionManager connectionManager) {
        this.venueRepository = venueRepository;
        this.databaseConnectionManager = connectionManager;
    }
    public static synchronized VenueService getInstance(VenueRepository venueRepository,DatabaseConnectionManager connectionManager){
        if(instance == null){
            instance = new VenueService(venueRepository,connectionManager);
        }
        return instance;
    }


    public List<Venue> getAllVenue() throws Exception {
        Connection connection = databaseConnectionManager.nextConnection();
        List<Venue> venues = null;
        try {
            venues = new ArrayList<>(venueRepository.getAllVenue(connection).values());  // 转换 Collection<Venue> 为 List<Venue>
            return venues;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
