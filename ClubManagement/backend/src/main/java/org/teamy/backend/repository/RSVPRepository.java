package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.RSVPDataMapper;
import org.teamy.backend.model.RSVP;

import java.sql.Connection;
import java.sql.SQLException;

public class RSVPRepository {
    private final RSVPDataMapper rsvpDataMapper;
    private static RSVPRepository instance;

    private RSVPRepository(RSVPDataMapper rsvpDataMapper) {
        this.rsvpDataMapper = rsvpDataMapper;
    }
    public static synchronized RSVPRepository getInstance(RSVPDataMapper rsvpDataMapper){
        if(instance == null){
            instance = new RSVPRepository(rsvpDataMapper);
        }
        return instance;
    }
    public RSVP findRSVPById(int Id) throws Exception {
        return rsvpDataMapper.findRSVPById(Id);
    }
    public void saveRSVP(Connection connection,RSVP rsvp) throws SQLException {
        rsvpDataMapper.saveRSVP(connection,rsvp);
    }
}
