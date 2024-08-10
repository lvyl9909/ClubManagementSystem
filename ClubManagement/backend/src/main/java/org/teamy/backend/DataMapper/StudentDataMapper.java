package org.teamy.backend.DataMapper;

import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDataMapper {
    private Connection connection;

    public StudentDataMapper(Connection connection) {
        this.connection = connection;
    }
    public Student findStudentById(int studentId) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE student_id = ?");
        stmt.setInt(1, studentId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Student( rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("student_id"));
        }
        return null;
    }

    public void saveStudent(Student student) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO students (student_id, name, email,phone_number) VALUES (?, ?, ?,?)");
        stmt.setString(1, student.getStudentId());
        stmt.setString(2, student.getName());
        stmt.setString(3, student.getEmail());
        stmt.setLong(4, student.getPhoneNumber());
        stmt.executeUpdate();
    }
}
