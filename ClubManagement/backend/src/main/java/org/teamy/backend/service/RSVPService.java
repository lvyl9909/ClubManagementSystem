package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.repository.FundingApplicationRepository;
import org.teamy.backend.repository.RSVPRepository;

public class RSVPService {
    private final RSVPRepository rsvpRepository;
    private static RSVPService instance;
    public static synchronized RSVPService getInstance(RSVPRepository rsvpRepository) {
        if (instance == null) {
            instance = new RSVPService(rsvpRepository);
        }
        return instance;
    }
    private RSVPService(RSVPRepository rsvpRepository) {
        this.rsvpRepository = rsvpRepository;
    }
    public RSVP getRSVPById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        RSVP rsvp = rsvpRepository.findRSVPById(id);
        return rsvp;
    }
}
