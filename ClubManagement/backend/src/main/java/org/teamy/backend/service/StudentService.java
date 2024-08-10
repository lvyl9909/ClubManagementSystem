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
}
