package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.*;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.EventService;
import org.teamy.backend.service.StudentService;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@WebServlet("/student/students/*")
public class StudentController  extends HttpServlet {
    StudentService studentService;

    ClubService clubService;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        clubService = (ClubService) getServletContext().getAttribute(ContextListener.CLUB_SERVICE);
        studentService = (StudentService) getServletContext().getAttribute(ContextListener.STUDENT_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
        System.out.println("success init");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id"); // 获取查询字符串中的 "id" 参数
        String nameParam = req.getParameter("query"); // 获取查询字符串中的 "title" 参数

        RequestHandler handler = () -> {
            if (nameParam != null && !nameParam.trim().isEmpty()) {
                return searchStudent(nameParam);  // 如果有 title 参数，则进行模糊搜索
            }else if (Objects.equals(idParam, "-1")) {
                return listStudents();
            }
            return null;
        };
        MarshallingRequestHandler.of(mapper, resp, handler).handle();
    }

//    private ResponseEntity listClubs() {
//        List<Club> clubs = studentService.getLazyLoadedClubs(studentService.getCurrentStudent());
//        return ResponseEntity.ok(clubs);
//    }

    private void viewStudent(HttpServletRequest req, HttpServletResponse resp, String studentId) throws IOException {
        resp.getWriter().write("Viewing student with ID: " + studentId);

    }

    private ResponseEntity listStudents() {
        try {
            List<Student> students = studentService.getAllStudent();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private ResponseEntity searchStudent(String parameter){
        try {
            List<Student> students =studentService.searchStudent(parameter);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
