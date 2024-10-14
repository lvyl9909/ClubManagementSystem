package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.*;
import org.teamy.backend.model.exception.OptimisticLockingFailureException;

import java.sql.Connection;
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
    public FundingApplication findFundingApplicationsByIdsWithLock(int id,Connection connection) {
        try {
            // 使用 SELECT ... FOR UPDATE 来锁定对应的记录
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM fundingapplications WHERE application_id = ? FOR UPDATE");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 获取 fundingapplication 的 status
                String statusString = rs.getString("status");
                fundingApplicationStatus status = fundingApplicationStatus.fromString(statusString);

                // 返回 fundingApplication 对象
                return new FundingApplication(
                        rs.getInt("application_id"),
                        rs.getString("description"),
                        rs.getBigDecimal("amount"),
                        rs.getInt("semester"),
                        rs.getInt("club"),
                        status,
                        rs.getDate("date"),
                        rs.getInt("reviewer"),
                        rs.getInt("version")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public FundingApplication findFundingApplicationsById(int id,Connection connection) {
        try {
            // 使用 SELECT ... FOR UPDATE 来锁定对应的记录
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM fundingapplications WHERE application_id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 获取 fundingapplication 的 status
                String statusString = rs.getString("status");
                fundingApplicationStatus status = fundingApplicationStatus.fromString(statusString);

                // 返回 fundingApplication 对象
                return new FundingApplication(
                        rs.getInt("application_id"),
                        rs.getString("description"),
                        rs.getBigDecimal("amount"),
                        rs.getInt("semester"),
                        rs.getInt("club"),
                        status,
                        rs.getDate("date"),
                        rs.getInt("reviewer"),
                        rs.getInt("version")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public List<FundingApplication> findAllApplication()throws SQLException{
        var connection = databaseConnectionManager.nextConnection();
        List<FundingApplication> fundingApplications = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM fundingapplications");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String statusString = rs.getString("status");
                fundingApplicationStatus status = fundingApplicationStatus.fromString(statusString);
                FundingApplication fundingApplication = new FundingApplication(rs.getInt("application_id"),rs.getString("description"),
                        rs.getBigDecimal("amount"), rs.getInt("semester"),
                        rs.getInt("club"), status,
                        rs.getDate("date"), rs.getInt("reviewer"),rs.getInt("version"));
                fundingApplications.add(fundingApplication);
            }
            return fundingApplications;
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
    public boolean saveFundingApplication(FundingApplication fundingApplication, Connection conn) throws SQLException {
        String query = "INSERT INTO fundingapplications (description, amount, semester, club, status, date) VALUES (?, ?, ?, ?, ?::funding_application_status, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fundingApplication.getDescription());
            stmt.setBigDecimal(2, fundingApplication.getAmount());
            stmt.setInt(3, fundingApplication.getSemester());
            stmt.setInt(4, fundingApplication.getClubId());
            stmt.setString(5, fundingApplicationStatus.Submitted.name());
            stmt.setDate(6, fundingApplication.getSqlDate());

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting funding application: " + e.getMessage());
        }
    }

    public List<Integer> findApplicationIdByClubId(Integer clubId,Connection connection){
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
                        rs.getInt("reviewer"),
                        rs.getInt("version")
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

    public boolean reviewFundingApplication(FundingApplication application, int reviewerId, String stat, Connection connection) {
        try {

            // 更新事件状态
            PreparedStatement updateStmt = connection.prepareStatement("UPDATE fundingapplications SET status = ?::funding_application_status, reviewer = ? WHERE application_id = ?");
            updateStmt.setString(1, stat);
            updateStmt.setInt(2, reviewerId);
            updateStmt.setInt(3, application.getId());  // 获取 FundingApplication 的 ID

            int rowsAffected = updateStmt.executeUpdate();  // 执行更新

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public boolean rejectFundingApplication(int applicationId,int reviewerId) {
        var connection = databaseConnectionManager.nextConnection();

        try {
            // 更新事件状态为 "Cancelled"
            PreparedStatement stmt = connection.prepareStatement("UPDATE fundingapplications SET status = ?::funding_application_status, reviewer = ? WHERE application_id = ?");
            stmt.setString(1, "Rejected");
            stmt.setInt(2, reviewerId);
            stmt.setInt(3, applicationId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected>0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public boolean updateFundingApplication(FundingApplication fundingApplication, Connection connection) throws Exception {
        // SQL 查询增加了对版本号的检查，确保乐观锁的机制生效
        String query = "UPDATE fundingapplications SET description = ?, amount = ?, semester = ?, club = ?, date = ?, reviewer = ?, version = version + 1 " +
                "WHERE application_id = ? AND version = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置参数
            stmt.setString(1, fundingApplication.getDescription());
            stmt.setBigDecimal(2, fundingApplication.getAmount());
            stmt.setInt(3, fundingApplication.getSemester());
            stmt.setInt(4, fundingApplication.getClubId());
            stmt.setDate(5, java.sql.Date.valueOf(fundingApplication.getDate()));  // 假设 getDate() 返回 LocalDate
            stmt.setLong(6, fundingApplication.getReviewerId());
            stmt.setInt(7, fundingApplication.getId());
            stmt.setInt(8, fundingApplication.getVersion());  // 设置版本号参数

            // 执行更新操作
            int rowsAffected = stmt.executeUpdate();

            // 判断是否成功更新行，若版本号不匹配则没有更新行
            if (rowsAffected == 0) {
                throw new OptimisticLockingFailureException("Funding application has been modified by another transaction (optimistic locking failed).");
            }

            // 更新成功，手动递增 FundingApplication 对象中的版本号
            fundingApplication.setVersion(fundingApplication.getVersion() + 1);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error updating FundingApplication: " + e.getMessage());
        }
    }
    public void lockFundingApplicationByClubId(int clubId, Connection conn) throws SQLException {
        String lockQuery = "SELECT * FROM fundingapplications WHERE club = ? FOR UPDATE";
        try (PreparedStatement lockStatement = conn.prepareStatement(lockQuery)) {
            lockStatement.setInt(1, clubId);
            lockStatement.executeQuery();
        }
    }

    public boolean existsByClubIdAndSemester(Integer clubId, Integer semester, Connection connection )throws SQLException {
        String query = "SELECT COUNT(*) FROM fundingapplications WHERE club = ? AND semester = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, clubId);
        stmt.setInt(2, semester);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 1;  // 如果有记录返回 true
        }

        return false;  // 没有记录则返回 false
    }
}
