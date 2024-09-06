package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.fundingApplication;
import org.teamy.backend.model.fundingApplicationStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class fundingApplicationMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    private ClubDataMapper clubDataMapper;
    private FacultyAdministratorMapper facultyAdministratorMapper;
    private EventDataMapper eventDataMapper;

    public fundingApplicationMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public fundingApplication findfundingApplicationById(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();
        List<Event> events;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM fundingapplications WHERE fundingapplications.application_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            //get fundingapplication status
            String statusString = rs.getString("fundingApplicationStatus");
            fundingApplicationStatus status = fundingApplicationStatus.fromString(statusString);

            //get relate event
            events = getRelatedEvents(rs.getInt("id"));


            if (rs.next()) {
                return new fundingApplication(rs.getString("description"),
                        rs.getBigDecimal("amount"), rs.getInt("semester"),
                        clubDataMapper.findClubById(rs.getInt("club")), status, events,
                        rs.getDate("date"), facultyAdministratorMapper.findFacultyAdministratorById(rs.getInt("reviewer")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }
        return null;
    }
    public List<Event> getRelatedEvents(int fundingApplicationId) throws Exception {
        var connection = databaseConnectionManager.nextConnection();
        List<Event> events = new ArrayList<>();


        String sql = "SELECT event_id FROM funding_application_events WHERE funding_application_id = ?";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, fundingApplicationId);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Event event = eventDataMapper.findEventById(rs.getInt("event_id"));
            events.add(event);
        }

        // 关闭资源
        rs.close();
        stmt.close();

        return events;
    }
}
