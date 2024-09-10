package org.teamy.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.teamy.backend.DataMapper.StudentDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.RSVP;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.Ticket;
import org.teamy.backend.repository.StudentClubRepository;
import org.teamy.backend.repository.StudentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentService {
    private StudentRepository studentRepository;
    private static StudentService instance;
    public static synchronized StudentService getInstance(StudentRepository studentRepository) {
        if (instance == null) {
            instance = new StudentService(studentRepository);
        }
        return instance;
    }
    private StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;

    }
    public UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }
    public Student getCurrentStudent() {
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails instanceof Student) {
            return (Student) userDetails;
        }
        throw new IllegalStateException("Authenticated user is not a Student");
    }
    public Student getStudentById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Student student = null;
        try {
            student = studentRepository.findStudentById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return student;
    }
    public List<Club> getClub(Student student){
        System.out.println("current student club:"+student.getClubs());
        if (student.getClubs()==null||student.getClubs().isEmpty()){
            student = studentRepository.lazyLoadClub(student);
        }
        return student.getClubs();
    }
    public List<Ticket> getTicket(Student student){
        if (student.getClubs()==null){
            student = studentRepository.lazyLoadTicket(student);
        }
        return student.getTickets();
    }
    public List<RSVP> getRSVP(Student student){
        if (student.getClubs()==null){
            student = studentRepository.lazyLoadRSVP(student);
        }
        return student.getRsvps();
    }
    public List<Student> getAllStudent(){
        try {
            return studentRepository.getAllStudent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Student> searchStudent(String parameters) {
        Map<Long, Student> studentsMap = new HashMap<>();  // 使用学生 ID 去重
        List<Student> studentsByName;
        List<Student> studentsByEmail;

        try {
            // 模糊搜索按名字
            studentsByName = studentRepository.findStudentByName(parameters);
            for (Student student : studentsByName) {
                studentsMap.put(student.getId(), student);  // 以学生 ID 为键，去重
            }

            // 模糊搜索按邮箱
            studentsByEmail = studentRepository.findStudentByEmail(parameters);
            for (Student student : studentsByEmail) {
                studentsMap.put(student.getId(), student);  // 再次按学生 ID 存入 Map，若有重复会自动覆盖
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 返回去重后的学生列表
        return new ArrayList<>(studentsMap.values());
    }

}
