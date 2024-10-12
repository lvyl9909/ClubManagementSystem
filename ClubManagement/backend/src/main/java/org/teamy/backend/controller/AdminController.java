package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.FacultyAdministrator;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.Student;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.FundingApplicationService;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/fundingapplication/*")
public class AdminController extends HttpServlet {
    FundingApplicationService fundingApplicationService;
    ClubService clubService;
    private ObjectMapper mapper;
    @Override
    public void init() throws ServletException {
        fundingApplicationService = (FundingApplicationService) getServletContext().getAttribute(ContextListener.FUNDING_APPLICATION_SERVICE);
        clubService = (ClubService) getServletContext().getAttribute(ContextListener.CLUB_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 根据 ID 获取事件
        System.out.println("get all application");
        RequestHandler handler = this::viewAllApplication;
        MarshallingRequestHandler.of(mapper, resp, handler).handle();
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // Gets the path info of the URL
        if (pathInfo.equals("/approve")) {
            MarshallingRequestHandler.of(
                    mapper, // 使用Jackson的ObjectMapper
                    resp,
                    () -> approveApplication(req)
            ).handle();
        } else if (pathInfo.equals("/reject")) {
            MarshallingRequestHandler.of(
                    mapper, // 使用Jackson的ObjectMapper
                    resp,
                    () -> rejectApplication(req)
            ).handle();
        }
    }
    private ResponseEntity approveApplication(HttpServletRequest req) {
        try {
            String idParam = req.getParameter("id"); // 获取查询字符串中的 "id" 参数
            System.out.println("approve:"+idParam);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = null;
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                userDetails = (UserDetails) authentication.getPrincipal();
            }
            FacultyAdministrator facultyAdministrator=null;
            //if (userDetails instanceof FacultyAdministrator) {
                facultyAdministrator =  (FacultyAdministrator) userDetails;
            //}
            System.out.println(authentication.getPrincipal());
            // 调用删除事件的方法
            if (idParam == null) {
                throw new IllegalArgumentException("Missing 'id' parameter.");
            }

            fundingApplicationService.approveFundingApplication(Integer.valueOf(idParam),Math.toIntExact(facultyAdministrator.getId()));
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
                            .message("An error occurred while deleting events.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }
    private ResponseEntity rejectApplication(HttpServletRequest req) {
        try {
            String idParam = req.getParameter("id"); // 获取查询字符串中的 "id" 参数
            System.out.println("approve:"+idParam);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = null;
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                userDetails = (UserDetails) authentication.getPrincipal();
            }
            FacultyAdministrator facultyAdministrator=null;
            if (userDetails instanceof FacultyAdministrator) {
                facultyAdministrator =  (FacultyAdministrator) userDetails;
            }
            if (facultyAdministrator==null){
                throw new IllegalArgumentException("Missing facultyAdministrator.");
            }
            // 调用删除事件的方法
            if (idParam == null) {
                throw new IllegalArgumentException("Missing 'id' parameter.");
            }

            fundingApplicationService.rejectFundingApplication(Integer.valueOf(idParam),Math.toIntExact(facultyAdministrator.getId()));
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
                            .message("An error occurred while deleting events.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }
    private ResponseEntity viewAllApplication() {
        try {
            List<FundingApplication> fundingApplications =fundingApplicationService.getAllFundingApplication();
            for (FundingApplication fundingApplication : fundingApplications) {
                fundingApplication.setClub(clubService.getClubById(fundingApplication.getClubId()));
                System.out.println(fundingApplications);
            }
            return ResponseEntity.ok(fundingApplications);
        } catch (NumberFormatException e) {
            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                    Error.builder()
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .message("Invalid ID format")
                            .reason(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Error.builder()
                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .message("error")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }
}
