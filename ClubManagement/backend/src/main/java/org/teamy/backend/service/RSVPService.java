package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.RSVP;

public class RSVPService {
    private final RSVPDataMapper rsvpDataMapper;

    public RSVPService(RSVPDataMapper rsvpDataMapper) {
        this.rsvpDataMapper = rsvpDataMapper;
    }
    public RSVP getRSVPById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        RSVP rsvp = rsvpDataMapper.findRSVPById(id);
        return rsvp;
    }
}
