package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.StudentClubDataMapper;
import org.teamy.backend.DataMapper.VenueDataMapper;
import org.teamy.backend.model.Venue;

import java.util.List;

public class VenueRepository {
    private final VenueDataMapper venueDataMapper;
    private List<Venue> venueList;
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
    public List<Venue> getAllVenue() {
        if (venueList==null){
            venueList = venueDataMapper.getAllVenue();
        }
        return venueList;
    }
}
