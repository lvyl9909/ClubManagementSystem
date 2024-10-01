package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.EventService;
import org.teamy.backend.service.FundingApplicationService;
import org.teamy.backend.service.StudentService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet("/student/fundingappliction/*")
public class FundingApplicationController extends HttpServlet {
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
        String idParam = req.getParameter("clubid"); // 获取查询字符串中的 "id" 参数
        RequestHandler handler = () -> {

            Integer clubId = Integer.valueOf(idParam);
            return viewAllApplication(clubId);  // 根据 ID 获取事件

        };
        MarshallingRequestHandler.of(mapper, resp, handler).handle();
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // Gets the path info of the URL
        if (pathInfo.equals("/save")) {
            MarshallingRequestHandler.of(
                    mapper, // 使用Jackson的ObjectMapper
                    resp,
                    () -> saveApplication(req)
            ).handle();
        }else if(pathInfo.equals("/update")){
            MarshallingRequestHandler.of(
                    mapper, // 使用Jackson的ObjectMapper
                    resp,
                    () -> updateApplication(req)
            ).handle();
        }
    }

    private ResponseEntity saveApplication(HttpServletRequest req) {
        try {
            // 解析请求体中的Club数据，假设请求体是JSON格式
            FundingApplication fundingApplication = parseApplictionFromRequest(req);

            // 调用Service层保存Club
            boolean isSaved = fundingApplicationService.saveFundingApplication(fundingApplication);

            if (isSaved) {
                return ResponseEntity.ok(null);
            } else {
                return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                        Error.builder()
                                .status(HttpServletResponse.SC_BAD_REQUEST)
                                .message("Failed to save the event.")
                                .reason("Failed to save the event.")
                                .build()
                );
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                    Error.builder()
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .message("Failed to save the event.")
                            .reason(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Error.builder()
                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .message("An error occurred while saving the event.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }

    private ResponseEntity updateApplication(HttpServletRequest req) {
        try {
            // 解析请求体中的Club数据，假设请求体是JSON格式
            FundingApplication fundingApplication = parseApplictionFromRequest(req);

            // 调用Service层保存Club
            boolean isSaved = fundingApplicationService.updateFundingApplication(fundingApplication);

            if (isSaved) {
                return ResponseEntity.ok(null);
            } else {
                return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                        Error.builder()
                                .status(HttpServletResponse.SC_BAD_REQUEST)
                                .message("Failed to save the event.")
                                .reason("Failed to save the event.")
                                .build()
                );
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                    Error.builder()
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .message("Failed to save the event.")
                            .reason(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Error.builder()
                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .message("An error occurred while saving the event.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }

    private FundingApplication parseApplictionFromRequest(HttpServletRequest req) throws IOException {
        FundingApplication fundingApplication = mapper.readValue(req.getInputStream(), FundingApplication.class);

        if ( fundingApplication == null ) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        System.out.println(fundingApplication.toString());

        return fundingApplication;
    }

    private ResponseEntity viewAllApplication(Integer clubId) {
        try {
            Club club = clubService.getClubById(clubId);
            List<FundingApplication> fundingApplications =clubService.getFundingApplication(club);
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
