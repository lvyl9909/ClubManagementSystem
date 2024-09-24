package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FundingApplicationMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    private static FundingApplicationMapper instance;
    public static synchronized FundingApplicationMapper getInstance(DatabaseConnectionManager dbManager) {
        if (instance == null) {
            instance = new FundingApplicationMapper(dbManager);
        }
        return instance;
    }
    private FundingApplicationMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public FundingApplication findfundingApplicationById(int Id) {
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
    public List<Integer> findApplicationIdByClubId(Integer clubId){
        var connection = databaseConnectionManager.nextConnection();
        List<Integer> applicationsId= new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT application_id FROM fundingapplications WHERE club = ?");
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                applicationsId.add(rs.getInt("application_id"));
            }
            return applicationsId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }
    }
    public List<Integer> findApplicationIdByReviewerId(Integer reviewerId){
        var connection = databaseConnectionManager.nextConnection();
        List<Integer> applicationsId= new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT application_id FROM fundingapplications WHERE reviewer = ?");
            stmt.setInt(1, reviewerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                applicationsId.add(rs.getInt("application_id"));
            }
            return applicationsId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }
    }
    public List<FundingApplication> findFundingApplicationsByIds(List<Integer> applicationIds) throws SQLException {
        // 如果 applicationIds 列表为空，则返回空列表
        if (applicationIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建 SQL 查询，使用 IN 子句来查询多个申请记录
        String query = "SELECT * FROM fundingapplications WHERE application_id IN (" +
                applicationIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";

        var connection = databaseConnectionManager.nextConnection();
        List<FundingApplication> fundingApplications = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // 遍历结果集，将每个 FundingApplication 实例化并加入列表
            while (rs.next()) {
                // 获取 fundingApplication 的状态
                String statusString = rs.getString("status");
                fundingApplicationStatus status = fundingApplicationStatus.fromString(statusString);

                // 根据需要加载关联的 Events 数据
                // List<Event> events = getRelatedEvents(rs.getInt("id"));

                FundingApplication fundingApplication = new FundingApplication(
                        rs.getInt("application_id"),
                        rs.getString("description"),
                        rs.getBigDecimal("amount"),
                        rs.getInt("semester"),
                        rs.getInt("club"),
                        status,
                        rs.getDate("date"),
                        rs.getInt("reviewer")
                );

                fundingApplications.add(fundingApplication);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching funding applications by IDs", e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return fundingApplications;
    }
}
