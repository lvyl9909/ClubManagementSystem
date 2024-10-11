package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.model.TicketStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    private static TicketDataMapper instance;
    public static synchronized TicketDataMapper getInstance(DatabaseConnectionManager dbManager) {
        if (instance == null) {
            instance = new TicketDataMapper(dbManager);
        }
        return instance;
    }
    private TicketDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Ticket findTicketById(int Id) {
        var connection = databaseConnectionManager.nextConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tickets WHERE ticket_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Ticket( rs.getInt("ticket_id"), rs.getInt("student_id"),rs.getInt("rsvp"), TicketStatus.valueOf(rs.getString("status")) ,rs.getInt("event_id")
                );

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public List<Ticket> findTicketsByIds(List<Integer> ticketIds) throws SQLException {
        // 如果 ticketIds 列表为空，则返回空列表
        if (ticketIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建 SQL 查询，使用 IN 子句来查询多个 ticket_id
        String query = "SELECT * FROM tickets WHERE ticket_id IN (" +
                ticketIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";

        var connection = databaseConnectionManager.nextConnection();
        List<Ticket> tickets = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // 遍历结果集，将每个 Ticket 实例化并加入列表
            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getInt("student_id"),
                        rs.getInt("rsvp"),
                        TicketStatus.valueOf(rs.getString("status")),  // 假设 status 是字符串并且能转换为枚举
                        rs.getInt("event_id")
                );
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching tickets by IDs", e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return tickets;
    }
    public void saveTicket(Connection connection,Ticket ticket) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO tickets (student_id, rsvp,status,event_id) VALUES (?, ?,?::ticket_status,?)",
                    Statement.RETURN_GENERATED_KEYS // 添加这行
            );
            System.out.println("studentid:"+ticket.getStudentId()+"RsvpId:"+ticket.getRsvpId()+"status:"+ticket.getStatus().name()+"EventId:"+ticket.getEventId());
            stmt.setInt(1, ticket.getStudentId());
            stmt.setInt(2, ticket.getRsvpId());
            stmt.setString(3,ticket.getStatus().name());
            stmt.setInt(4,ticket.getEventId());
            int rowsAffected = stmt.executeUpdate();  // 检查受影响的行数
            System.out.println(rowsAffected);
            if (rowsAffected == 0) {
                throw new SQLException("Inserting ticket failed, no rows affected.");
            }
            // 获取生成的主键
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt(1));  // 更新到 RSVP 对象中
                } else {
                    throw new SQLException("Creating Ticket failed, no ID obtained.");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();  // 打印异常信息
            throw new RuntimeException("Error inserting ticket: " + e.getMessage());
        } finally{
        }
    }
    public void saveTickets(List<Ticket> tickets) throws SQLException {
        String query = "INSERT INTO tickets (student_id, rsvp, status, event_id) VALUES (?, ?, ?::ticket_status, ?)";
        var connection = databaseConnectionManager.nextConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // 开始批量插入
            for (Ticket ticket : tickets) {
                stmt.setInt(1, ticket.getStudentId());
                stmt.setInt(2, ticket.getRsvpId());
                stmt.setString(3, ticket.getStatus().name());
                stmt.setInt(4, ticket.getEventId());

                // 将当前 Ticket 插入操作添加到批处理中
                stmt.addBatch();
            }

            // 执行批量插入操作
            int[] rowsAffected = stmt.executeBatch();

            // 检查插入是否成功
            for (int row : rowsAffected) {
                if (row == 0) {
                    throw new SQLException("Inserting ticket failed, no rows affected.");
                }
            }

            // 获取生成的主键并更新到每个 Ticket 对象
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                int i = 0;
                while (generatedKeys.next()) {
                    // 获取生成的主键并设置到对应的 Ticket 对象中
                    tickets.get(i).setId(generatedKeys.getInt(1));
                    i++;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting tickets: " + e.getMessage());
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
    public void deleteTicket(Integer ticketId)throws SQLException{
        var connection = databaseConnectionManager.nextConnection();
        try {
            System.out.println("start delete");
            // 更新事件状态为 "Cancelled"
            PreparedStatement stmt = connection.prepareStatement("UPDATE tickets SET status = ?::ticket_status WHERE ticket_id = ?");
            stmt.setString(1, TicketStatus.Cancelled.name());
            stmt.setInt(2, ticketId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("influence row:"+rowsAffected);
            if (rowsAffected == 0) {
                throw new RuntimeException("No ticket found with id: " + ticketId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
    public void deleteTicket(Connection connection,Integer ticketId)throws SQLException{
        try {
            System.out.println("start delete");
            // 更新事件状态为 "Cancelled"
            PreparedStatement stmt = connection.prepareStatement("UPDATE tickets SET status = ?::ticket_status WHERE ticket_id = ?");
            stmt.setString(1, TicketStatus.Cancelled.name());
            stmt.setInt(2, ticketId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("influence row:"+rowsAffected);
            if (rowsAffected == 0) {
                throw new RuntimeException("No ticket found with id: " + ticketId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public List<Ticket> getTicketsFromEvent(Integer eventId) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        List<Ticket> tickets = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tickets WHERE event_id = ?");
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket( rs.getInt("ticket_id"), rs.getInt("student_id"),rs.getInt("rsvp"), TicketStatus.valueOf(rs.getString("status")) ,rs.getInt("event_id")
                );
                tickets.add(ticket);
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return tickets;
    }

    public List<Ticket> getTicketsFromEvent(Connection connection,Integer eventId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tickets WHERE event_id = ?");
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket( rs.getInt("ticket_id"), rs.getInt("student_id"),rs.getInt("rsvp"), TicketStatus.valueOf(rs.getString("status")) ,rs.getInt("event_id")
                );
                tickets.add(ticket);
            }
        } finally {
        }

        return tickets;
    }

    public List<Integer> getTicketsIdFromStudent(Integer studentId) {
        var connection = databaseConnectionManager.nextConnection();
        List<Integer> tickets = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT ticket_id FROM tickets WHERE student_id = ?");
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tickets.add(rs.getInt("ticket_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return tickets;
    }
}
