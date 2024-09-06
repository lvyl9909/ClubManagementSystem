package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.EventService;
import org.teamy.backend.service.StudentClubService;
import org.teamy.backend.service.StudentService;

import java.io.IOException;

@WebServlet("/student/admin/*")
public class StudentClubController extends HttpServlet {
    StudentClubService studentClubService;
    private ObjectMapper mapper;
    @Override
    public void init() throws ServletException {
        studentClubService = (StudentClubService) getServletContext().getAttribute(ContextListener.STUDENT_CLUB_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
        System.out.println("success init");
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();  // 获取 URL 的路径信息

        if (pathInfo.equals("/add")) {
            MarshallingRequestHandler.of(
                    mapper,
                    resp,
                    () -> addAdmin(req)
            ).handle();
        } else if (pathInfo.equals("/delete")) {
            MarshallingRequestHandler.of(
                    mapper,
                    resp,
                    () -> deleteAdmin(req)
            ).handle();
        }
    }

    private ResponseEntity deleteAdmin(HttpServletRequest req) {
        try {
            // 从请求体中解析 clubId 和 studentId
            Integer clubId = Integer.parseInt(req.getParameter("clubId"));
            Integer studentId = Integer.parseInt(req.getParameter("studentId"));

            // 调用服务层方法
            studentClubService.deleteAdmin(clubId, studentId);

            return ResponseEntity.ok(null);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                    Error.builder()
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .message("Invalid request parameters.")
                            .reason(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Error.builder()
                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .message("An error occurred while deleting the admin.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }

    private ResponseEntity addAdmin(HttpServletRequest req) {
        try {
            // 从请求体中解析 clubId 和 studentId
            Integer clubId = Integer.parseInt(req.getParameter("clubId"));
            Integer studentId = Integer.parseInt(req.getParameter("studentId"));

            // 调用服务层方法
            studentClubService.addAdmin(clubId, studentId);

            return ResponseEntity.ok(null);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                    Error.builder()
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .message("Invalid request parameters.")
                            .reason(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Error.builder()
                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .message("An error occurred while adding the admin.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }
}
