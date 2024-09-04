package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.StudentService;

import java.io.IOException;
import java.util.List;

@WebServlet("/student/userdetailed/*")
public class UserDetailedController extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        String pathInfo = req.getPathInfo(); // Gets the path info of the URL

        RequestHandler handler = () -> {
            if (pathInfo.equals("/club")) {
                return listClubs();
            }else if (pathInfo.equals("/info")) {
                return viewStudent();
            }
            return null;
        };
        MarshallingRequestHandler.of(mapper, resp, handler).handle();

    }

    private ResponseEntity listClubs() {
        List<Club> clubs = studentService.getLazyLoadedClubs(studentService.getCurrentStudent());
        return ResponseEntity.ok(clubs);
    }
    private ResponseEntity viewStudent() {
        Student student = studentService.getCurrentStudent();
        return ResponseEntity.ok(student);
    }
}
