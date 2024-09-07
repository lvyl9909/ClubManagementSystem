package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;

    public StudentDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Student findStudentById(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();
        Student student = null;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                student = new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("pwd"),rs.getBoolean("isactive"));
                List<Integer> ticketsIds = getRelatedIds(connection, "tickets", "ticket_id", student.getId());
                student.setTicketsId(ticketsIds);
                // 查询 student_rsvp 表中的相关记录
                List<Integer> rsvpIds = getRelatedIds(connection, "rsvps", "rsvp_id", student.getId());
                student.setRsvpsId(rsvpIds);

                // 查询 student_club 表中的相关记录
                List<Integer> clubIds = getRelatedIds(connection, "students_clubs", "club_id", student.getId());
                student.setClubId(clubIds);

                return student;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public Student findStudentByUsername(String username) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        Student student = null;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Finish search");
            if (rs.next()) {
                student = new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("pwd"),rs.getBoolean("isactive"));
                System.out.println("start searching ticket");
                List<Integer> ticketsIds = getRelatedIds(connection, "tickets", "ticket_id", student.getId());
                student.setTicketsId(ticketsIds);
                System.out.println("end searching ticket");

                // 查询 student_rsvp 表中的相关记录
                System.out.println("start searching rsvp");
                List<Integer> rsvpIds = getRelatedIds(connection, "rsvps", "rsvp_id", student.getId());
                student.setRsvpsId(rsvpIds);
                System.out.println("end searching rsvp");


                // 查询 student_club 表中的相关记录
                System.out.println("start searching clubs");
                List<Integer> clubIds = getRelatedIds(connection, "students_clubs", "club_id", student.getId());
                student.setClubId(clubIds);
                System.out.println("end searching clubs");

                return student;
            }
        }catch (SQLException e){
            throw new SQLException(e.getMessage());
        } finally{
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    private List<Integer> getRelatedIds(Connection connection, String tableName, String columnName, Long studentId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT " + columnName + " FROM " + tableName + " WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt(columnName));
            }
        }
        return ids;
    }

    public List<Student> getAllStudent(){
        var connection = databaseConnectionManager.nextConnection();
        List<Student> students = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("pwd"),rs.getBoolean("isactive"));
                students.add(student);
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }
    public List<Student> findStudentByName(String name) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        List<Student> students = null;
        try {
            students = new ArrayList<>();

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE LOWER(name) LIKE LOWER(?)");
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("pwd"),rs.getBoolean("isactive"));

                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return students;
    }
    public List<Student> findStudentByEmail(String email) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        List<Student> students = null;
        try {
            students = new ArrayList<>();

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE LOWER(email) LIKE LOWER(?)");
            stmt.setString(1, "%" + email + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("pwd"),rs.getBoolean("isactive"));

                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return students;
    }

//    public void saveStudent(Student student) throws Exception {
//        var connection = databaseConnectionManager.nextConnection();
//
//        try {
//            PreparedStatement stmt = connection.prepareStatement("INSERT INTO students (studentid, name, email,phone_number,passw,isactive) VALUES (?, ?, ?,?,?,?)");
//            stmt.setString(1, student.getStudentId());
//            stmt.setString(2, student.getName());
//            stmt.setString(3, student.getEmail());
//            stmt.setLong(4, student.getPhoneNumber());
//            stmt.setString(5, student.getPassword());
//            stmt.setBoolean(6, true);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } finally {
//            databaseConnectionManager.releaseConnection(connection);
//        }
//    }

//    public boolean updateStudent(Student student) throws Exception {
//        var connection = databaseConnectionManager.nextConnection();
//
//        // SQL update
//        String sql = "UPDATE students SET name = ?, email = ?, phone_number = ? WHERE studentid = ?";
//        int rowsAffected = 0;
//
//        try {
//            // Precompiled SQL statements
//            PreparedStatement stmt = connection.prepareStatement(sql);
//
//            // Set update argument
//            stmt.setString(1, student.getName());
//            stmt.setString(2, student.getEmail());
//            stmt.setLong(3, student.getPhoneNumber());
//            stmt.setString(4, student.getStudentId());
//
//            // Execution update
//            rowsAffected = stmt.executeUpdate();
//
//            // Close resource
//            stmt.close();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } finally {
//            databaseConnectionManager.releaseConnection(connection);
//
//        }
//
//        // If the number of affected rows is greater than 0, the update is successful
//        return rowsAffected > 0;
//    }
}
