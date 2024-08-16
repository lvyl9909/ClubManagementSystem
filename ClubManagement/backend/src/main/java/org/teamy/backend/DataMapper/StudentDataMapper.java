package org.teamy.backend.DataMapper;

import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDataMapper {
    private Connection connection;

    public StudentDataMapper(Connection connection) {
        this.connection = connection;
    }
    public Student findStudentById(int Id) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE id = ?");
        stmt.setInt(1, Id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Student( rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("student_id"));
        }
        return null;
    }
    public Student findStudentByStudentId(String studentId) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE studentid = ?");
        stmt.setString(1, studentId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Student( rs.getString("name"), rs.getString("email"),rs.getLong("phone_number"),rs.getString("student_id"));
        }
        return null;
    }
    public List<Student> findStudentByName(String name) throws Exception {
        List<Student> students = new ArrayList<>();

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

        return students;
    }

    public void saveStudent(Student student) throws Exception {
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO students (studentid, name, email,phone_number) VALUES (?, ?, ?,?)");
        stmt.setString(1, student.getStudentId());
        stmt.setString(2, student.getName());
        stmt.setString(3, student.getEmail());
        stmt.setLong(4, student.getPhoneNumber());
        stmt.executeUpdate();
    }

    public boolean updateStudent(Student student) throws Exception {
        // SQL update
        String sql = "UPDATE students SET name = ?, email = ?, phone_number = ? WHERE studentid = ?";

        // Precompiled SQL statements
        PreparedStatement stmt = connection.prepareStatement(sql);

        // Set update argument
        stmt.setString(1, student.getName());
        stmt.setString(2, student.getEmail());
        stmt.setLong(3, student.getPhoneNumber());
        stmt.setString(4, student.getStudentId());

        // Execution update
        int rowsAffected = stmt.executeUpdate();

        // Close resource
        stmt.close();

        // If the number of affected rows is greater than 0, the update is successful
        return rowsAffected > 0;
    }
}
