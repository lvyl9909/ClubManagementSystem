package org.teamy.backend.service;

import org.teamy.backend.DataMapper.StudentDataMapper;
import org.teamy.backend.model.Student;

public class StudentService {
    private StudentDataMapper studentDataMapper;

    public StudentService(StudentDataMapper studentDataMapper) {
        this.studentDataMapper = studentDataMapper;
    }

    public boolean updateStudent(Student student) throws Exception {
        // 可以在这里添加额外的业务逻辑，比如数据验证
        if (student.getName() == null || student.getName().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }

        // 调用DAO层的方法
        return studentDataMapper.updateStudent(student);
    }

    public Student getStudentByStudentId(String studentId) throws Exception {
        // 在这里可以添加业务逻辑，比如检查studentId是否为空
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }

        Student student = studentDataMapper.findStudentByStudentId(studentId);

        if (student == null) {
            throw new Exception("Student with ID " + studentId + " not found");
        }

        return student;
    }


}
