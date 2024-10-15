package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.StudentClubDataMapper;
import org.teamy.backend.DataMapper.VenueDataMapper;
import org.teamy.backend.model.Venue;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VenueRepository {
    private final VenueDataMapper venueDataMapper;
    private Map<Integer,Venue> venueList;
    private static VenueRepository instance;

    private VenueRepository(VenueDataMapper venueDataMapper) {
        this.venueDataMapper = venueDataMapper;
    }
    public static synchronized VenueRepository getInstance(VenueDataMapper venueDataMapper){
        if(instance == null){
            instance = new VenueRepository(venueDataMapper);
        }
        return instance;
    }
//    public List<Venue> getAllVenue() {
//        if (venueList==null){
//            venueList = venueDataMapper.getAllVenue();
//        }
//        return venueList;
//    }

    public Map<Integer, Venue> getAllVenue(Connection connection) {
        if (venueList == null || venueList.isEmpty()) {
            // Convert from List<Venue> to Map<Venue.getId(), Venue>
            venueList = venueDataMapper.getAllVenue(connection).stream()
                    .collect(Collectors.toMap(Venue::getId, venue -> venue));
        }
        return venueList;
    }

    public Venue getVenueById(Integer Id,Connection connection){
        if (venueList == null || venueList.isEmpty()) {
            // Convert from List<Venue> to Map<Venue.getId(), Venue>
            venueList = venueDataMapper.getAllVenue(connection).stream()
                    .collect(Collectors.toMap(Venue::getId, venue -> venue));
        }
        return venueList.get(Id);
    }
}
