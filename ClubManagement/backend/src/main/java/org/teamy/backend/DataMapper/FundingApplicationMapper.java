package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FundingApplicationMapper {
    private final DatabaseConnectionManager databaseConnectionManager;

    public FundingApplicationMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public FundingApplication findfundingApplicationById(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();
        List<Event> events;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM fundingapplications WHERE fundingapplications.application_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            //get fundingapplication status
                String statusString = rs.getString("status");
                fundingApplicationStatus status = fundingApplicationStatus.fromString(statusString);

            //get relate event
//            events = getRelatedEvents(rs.getInt("id"));


            if (rs.next()) {
                return new FundingApplication(rs.getInt("application_id"),rs.getString("description"),
                        rs.getBigDecimal("amount"), rs.getInt("semester"),
                        rs.getInt("club"), status,
                        rs.getDate("date"), rs.getInt("reviewer"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public List<FundingApplication> findApplicationByClubId(Integer clubId)throws SQLException{
        var connection = databaseConnectionManager.nextConnection();
        List<FundingApplication> fundingApplications = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM fundingapplications WHERE club = ?");
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String statusString = rs.getString("status");
                fundingApplicationStatus status = fundingApplicationStatus.fromString(statusString);
                FundingApplication fundingApplication = new FundingApplication(rs.getInt("application_id"),rs.getString("description"),
                        rs.getBigDecimal("amount"), rs.getInt("semester"),
                        rs.getInt("club"), status,
                        rs.getDate("date"), rs.getInt("reviewer"));
                fundingApplications.add(fundingApplication);
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return fundingApplications;
    }
    public boolean saveFundingApplication(FundingApplication fundingApplication)throws SQLException{
        var connection = databaseConnectionManager.nextConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO fundingapplications (description, amount,semester,club,status,date) VALUES (?, ?,?,?,?::funding_application_status,?)"
            );
            stmt.setString(1, fundingApplication.getDescription());
            stmt.setBigDecimal(2, fundingApplication.getAmount());
            stmt.setInt(3,fundingApplication.getSemester());
            stmt.setInt(4,fundingApplication.getClubId());
            stmt.setString(5,fundingApplicationStatus.Submitted.name());
            stmt.setDate(6,fundingApplication.getSqlDate());
            int rowsAffected = stmt.executeUpdate();  // 检查受影响的行数
            System.out.println(rowsAffected);
            if (rowsAffected == 0) {
                throw new SQLException("Inserting ticket failed, no rows affected.");
            }else {
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();  // 打印异常信息
            throw new RuntimeException("Error inserting ticket: " + e.getMessage());
        } finally{
            databaseConnectionManager.releaseConnection(connection);
        }
    }
}
