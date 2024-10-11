package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.EventStatus;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.exception.OptimisticLockingFailureException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    private static EventDataMapper instance;
    public static synchronized EventDataMapper getInstance(DatabaseConnectionManager dbManager) {
        if (instance == null) {
            instance = new EventDataMapper(dbManager);
        }
        return instance;
    }
    private EventDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Event findEventById(int Id,Connection connection) {

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE event_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event(
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getInt("venue"),            // 你可能需要确认 venue 是字符串还是 ID
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id"),
                        rs.getString("status"), // 将状态从数据库转换为枚举类型
                        rs.getInt("capacity"),
                        rs.getInt("version")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public List<Event> findEventsByIds(List<Integer> eventIds) throws SQLException {
        // 如果 eventIds 列表为空，则返回空列表
        if (eventIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建 SQL 查询，使用 IN 子句来查询多个事件
        String query = "SELECT * FROM events WHERE event_id IN (" +
                eventIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";

        var connection = databaseConnectionManager.nextConnection();
        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // 遍历结果集，将每个 Event 实例化并加入列表
            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getInt("venue"),            // 假设 venue 是 ID，如果是字符串，修改为 rs.getString("venue")
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id"),
                        rs.getString("status"),       // 根据需要将字符串转换为 Enum
                        rs.getInt("capacity"),
                        rs.getInt("version")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching events by IDs", e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return events;
    }
    public void deleteEvent(Connection connection, int eventId) {
        try {
            // 更新事件状态为 "Cancelled"
            PreparedStatement stmt = connection.prepareStatement("UPDATE events SET status = ?::event_status WHERE event_id = ?");
            stmt.setString(1, "Cancelled");
            stmt.setInt(2, eventId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No event found with id: " + eventId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Event> findEventsByTitle(String title) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE LOWER(title) LIKE LOWER(?)");
            stmt.setString(1, "%" + title + "%");  // 使用模糊匹配
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getInt("venue"),            // 你可能需要确认 venue 是字符串还是 ID
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id"),
                        rs.getString("status"), // 将状态从数据库转换为枚举类型
                        rs.getInt("capacity"),
                        rs.getInt("version")
                );
                events.add(event);
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return events;
    }

    public boolean saveEvent(Event event) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        String query = "INSERT INTO events (title, description,date,time,venue,cost,club_id,status) VALUES (?,?,?,?,?,?,?,?::event_status)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3,event.getSqlDate());
            stmt.setTime(4,event.getSqlTime());
            stmt.setInt(5,event.getVenueId());
            stmt.setBigDecimal(6,event.getCost());
            stmt.setInt(7,event.getClubId());
            stmt.setString(8, EventStatus.Ongoing.name());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error saving club: " + e.getMessage());
        }finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public List<Event> getAllEvent() {
        var connection = databaseConnectionManager.nextConnection();

        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events ");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getInt("venue"),            // 你可能需要确认 venue 是字符串还是 ID
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id"),
                        rs.getString("status"), // 将状态从数据库转换为枚举类型
                        rs.getInt("capacity"),
                        rs.getInt("version")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }

        return events;
    }
    public boolean updateEvent(Event event) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        // SQL 更新语句，更新指定的事件
        String query = "UPDATE events SET title = ?, description = ?, date = ?, time = ?, venue = ?, cost = ?, club_id = ?, status = ?::event_status,capacity = ? WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, event.getSqlDate());
            stmt.setTime(4, event.getSqlTime());
            stmt.setInt(5, event.getVenueId());
            stmt.setBigDecimal(6, event.getCost());
            stmt.setInt(7, event.getClubId());
            stmt.setString(8, event.getStatus().name());  // 假设状态是枚举类型
            stmt.setInt(9,event.getCapacity());
            stmt.setInt(10, event.getId());  // 使用 eventId 作为更新条件

            // 执行更新操作
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // 返回是否成功更新
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error updating event: " + e.getMessage());
        } finally {
            // 释放数据库连接
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public boolean updateEventCapacity(Event event, Connection connection) throws Exception {
        // SQL 更新语句，使用 version 进行乐观锁控制
        String query = "UPDATE events SET capacity = ?, version = version + 1 WHERE event_id = ? AND version = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, event.getCapacity());       // 设置新的 capacity
            stmt.setInt(2, event.getId());             // 使用 eventId 作为更新条件
            stmt.setInt(3, event.getVersion());        // 使用当前的 version 作为乐观锁检查条件

            // 执行更新操作
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                // 如果没有行被更新，说明 version 不匹配，抛出乐观锁异常
                throw new OptimisticLockingFailureException("Event version mismatch, update failed.");
            }

            // 更新成功后，递增 event 的本地 version
            event.setVersion(event.getVersion() + 1);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error updating event capacity: " + e.getMessage());
        }
    }

    public List<Integer> findEventIdByClubId(Integer clubId){
        var connection = databaseConnectionManager.nextConnection();
        List<Integer> eventsId= new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT event_id FROM events WHERE club_id = ?");
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                eventsId.add(rs.getInt("event_id"));
            }
            return eventsId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }
    }
}
