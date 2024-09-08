package org.teamy.backend.service;

import org.teamy.backend.DataMapper.VenueDataMapper;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.Venue;

import java.util.List;

public class VenueService {
    private final VenueDataMapper venueDataMapper;

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
