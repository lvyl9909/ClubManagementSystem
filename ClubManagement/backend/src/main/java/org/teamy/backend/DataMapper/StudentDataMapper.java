package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Person;
import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StudentDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    private static StudentDataMapper instance;
    public static synchronized StudentDataMapper getInstance(DatabaseConnectionManager dbManager) {
        if (instance == null) {
            instance = new StudentDataMapper(dbManager);
        }
        return instance;
    }
    private StudentDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Student findStudentById(int Id) {
        var connection = databaseConnectionManager.nextConnection();
        Student student = null;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ? AND role = 'student'");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("pwd"),rs.getBoolean("isactive"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public List<Student> findStudentsByIds(List<Integer> studentIds) throws SQLException {
        // 如果 studentIds 列表为空，则返回空列表
        if (studentIds.isEmpty()) {
            return new ArrayList<>();
        }

        String query = "SELECT * FROM students WHERE id IN (" +
                studentIds.stream().map(String::valueOf).collect(Collectors.joining(",")) +
                ") AND role = 'student'";

        var connection = databaseConnectionManager.nextConnection();
        List<Student> students = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // 遍历结果集，将每个学生实例化并加入列表
            while (rs.next()) {
                Student student = new Student(rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getLong("phone_number"),
                        rs.getString("pwd"),
                        rs.getBoolean("isactive"));
                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching students by IDs", e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return students;
    }
    public Person findUserByUsername(String username) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        Person user = null;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Finish search");
            if (rs.next()) {
                if (Objects.equals(rs.getString("role"), "student")){
                    user = new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("pwd"),rs.getBoolean("isactive"));
                }else {

                }
                return user;
            }
        }catch (SQLException e){
            throw new SQLException(e.getMessage());
        } finally{
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }

    public List<Student> getAllStudent(){
        var connection = databaseConnectionManager.nextConnection();
        List<Student> students = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users where role== 'student'");
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
    public List<Student> findStudentByName(String name) {
        var connection = databaseConnectionManager.nextConnection();

        List<Student> students = null;
        try {
            students = new ArrayList<>();

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE LOWER(name) LIKE LOWER(?) AND role = 'student'");
            stmt.setString(1, "%" + name + "%"); // Assuming you're setting the name for the LIKE clause
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getBoolean("isactive"));

                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return students;
    }
    public List<Student> findStudentByEmail(String email) {
        var connection = databaseConnectionManager.nextConnection();

        List<Student> students = null;
        try {
            students = new ArrayList<>();

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE LOWER(email) LIKE LOWER(?) AND role = 'student'");
            stmt.setString(1, "%" + email + "%"); // Assuming you're setting the name for the LIKE clause
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(rs.getLong("id"),rs.getString("username"), rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getBoolean("isactive"));

                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return students;
    }
}
