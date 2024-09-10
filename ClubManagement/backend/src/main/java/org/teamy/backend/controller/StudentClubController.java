package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.EventService;
import org.teamy.backend.service.StudentClubService;
import org.teamy.backend.service.StudentService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet("/student/admin/*")
public class StudentClubController extends HttpServlet {
    StudentClubService studentClubService;
    private ObjectMapper mapper;
    StudentService studentService;
    @Override
    public void init() throws ServletException {
        studentClubService = (StudentClubService) getServletContext().getAttribute(ContextListener.STUDENT_CLUB_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
        studentService = (StudentService) getServletContext().getAttribute(ContextListener.STUDENT_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");

        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // 获取用户的角色权限
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            // 打印用户的权限
            System.out.println("User Authorities: ");
            authorities.forEach(auth -> System.out.println(auth.getAuthority()));

            RequestHandler handler = () -> {
                if (idParam != null) {
                    try {
                        int id = Integer.parseInt(idParam);

                        // 检查用户是否有访问该 id 的权限
                        if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_CLUB_" + id))) {
                            // 用户有权限访问该学生，继续处理请求
                            return findAllStudent(id);
                        } else {
                            // 用户没有访问该 id 的权限，返回 403 Forbidden
                            return ResponseEntity.of(HttpServletResponse.SC_FORBIDDEN,
                                    Error.builder()
                                            .status(HttpServletResponse.SC_FORBIDDEN)
                                            .message("Access Denied")
                                            .reason("You do not have permission to access this student's data.")
                                            .build()
                            );
                        }
                    } catch (NumberFormatException e) {
                        return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                                Error.builder()
                                        .status(HttpServletResponse.SC_BAD_REQUEST)
                                        .message("Invalid ID format")
                                        .reason(e.getMessage())
                                        .build()
                        );
                    }
                }
                return null;
            };

            // 执行请求处理逻辑
            MarshallingRequestHandler.of(mapper, resp, handler).handle();
        } else {
            // 用户未认证，返回 401 Unauthorized
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to access this resource.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();  // 获取 URL 的路径信息
        System.out.println(pathInfo);
        if (pathInfo.equals("/add")) {
            MarshallingRequestHandler.of(
                    mapper,
                    resp,
                    () -> addAdmin(req)
            ).handle();
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // Gets the path info of the URL
        if (pathInfo.equals("/delete")) {
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

            System.out.println(clubId+studentId);
            // 调用服务层方法
            studentClubService.deleteAdmin(clubId, studentId);

            return ResponseEntity.delete(null);

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
    private ResponseEntity findAllStudent(int id) {
        List<Student> students = new ArrayList<>();
        try {
            List<Integer> studentsId = studentClubService.findStudentIdByClubId(id);
            for (Integer studentid : studentsId) {
                students.add(studentService.getStudentById(studentid));
            }
            return ResponseEntity.ok(students);
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
