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

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            PreparedStatement stmt2 = connection.prepareStatement("SELECT * FROM students_clubs WHERE student_id = ?");
            stmt2.setInt(1, Id);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs.next()) {
                Student tempStudent =  new Student( rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("studentid"));
                while (rs2.next()) {
                    tempStudent.addClubId(rs2.getInt("club_id"));
                }
                return tempStudent;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public Student findStudentByStudentId(String studentId) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE studentid = ?");
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Student( rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("studentid"),rs.getString("passw"));
            }
        }catch (SQLException e){
            throw new SQLException(e.getMessage());
        } finally{
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public List<Student> findStudentByName(String name) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        List<Student> students = null;
        try {
            students = new ArrayList<>();

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getLong("phone_number"),
                        rs.getString("student_id")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return students;
    }

    public void saveStudent(Student student) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO students (studentid, name, email,phone_number,passw,isactive) VALUES (?, ?, ?,?,?,?)");
            stmt.setString(1, student.getStudentId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setLong(4, student.getPhoneNumber());
            stmt.setString(5, student.getPassword());
            stmt.setBoolean(6, true);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public boolean updateStudent(Student student) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        // SQL update
        String sql = "UPDATE students SET name = ?, email = ?, phone_number = ? WHERE studentid = ?";
        int rowsAffected = 0;

        try {
            // Precompiled SQL statements
            PreparedStatement stmt = connection.prepareStatement(sql);

            // Set update argument
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setLong(3, student.getPhoneNumber());
            stmt.setString(4, student.getStudentId());

            // Execution update
            rowsAffected = stmt.executeUpdate();

            // Close resource
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }

        // If the number of affected rows is greater than 0, the update is successful
        return rowsAffected > 0;
    }

    public List<Club> findClubStudentJoin(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        List<Club> clubs = null;
        try {
            clubs = new ArrayList<>();

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students_clubs WHERE student_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Integer clubId = rs.getInt("club_id");
                stmt = connection.prepareStatement("SELECT * FROM clubs WHERE club_id = ?");
                stmt.setInt(1,clubId);
                ResultSet clubRs = stmt.executeQuery();

                if (clubRs.next()) {
                    Club club = new Club(
                            clubRs.getString("name"),
                            clubRs.getString("description")
                    );
                    clubs.add(club);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return clubs;
    }
}
