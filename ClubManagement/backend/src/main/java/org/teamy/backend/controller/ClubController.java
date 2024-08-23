package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.model.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;


@WebServlet("/clubs/*")
public class ClubController extends HttpServlet {
    private ClubService clubService;
    private ObjectMapper mapper;

    private Gson gson = new Gson();  // Gson instance

    @Override
    public void init() throws ServletException {
        clubService = (ClubService) getServletContext().getAttribute(ContextListener.CLUB_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
        System.out.println("success init");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idParam = req.getParameter("id"); // Gets the "id" argument from the query string
        String pathInfo = req.getPathInfo(); // Gets the path portion of the URL

        if (Objects.equals(idParam, "-1")) {
            listClubs(req, resp); // If there is no id parameter, all clubs are listed
        } else {
            try {
                Integer clubId = Integer.valueOf(idParam); // Converts the id argument to an integer
                viewClub(req, resp, clubId); // call viewClub method
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format"); // ID invalid, return 404
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
//        else {
//            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // return 404
//        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("processing");
        String pathInfo = req.getPathInfo(); // 获取URL路径信息

        if (pathInfo.equals("/save")) {
            MarshallingRequestHandler.of(
                    mapper, // 使用Jackson的ObjectMapper
                    resp,
                    () -> {
                        try {
                            // 解析请求体中的Club数据，假设请求体是JSON格式
                            Club club = parseClubFromRequest(req);

                            // 调用Service层保存Club
                            boolean isSaved = clubService.saveClub(club);

                            if (isSaved) {
                                return ResponseEntity.ok(null);
                            } else {
                                return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                                        Error.builder()
                                                .status(HttpServletResponse.SC_BAD_REQUEST)
                                                .message("Failed to save the club.")
                                                .reason("Failed to save the club.")
                                                .build()
                                );
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                                    Error.builder()
                                            .status(HttpServletResponse.SC_BAD_REQUEST)
                                            .message("Failed to save the club.")
                                            .reason(e.getMessage())
                                            .build()
                            );
                        } catch (Exception e) {
                            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                    Error.builder()
                                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                            .message("An error occurred while saving the club.")
                                            .reason(e.getMessage())
                                            .build()
                            );
                        }
                    }
            ).handle();
        }
    }

    private Club parseClubFromRequest(HttpServletRequest req) throws IOException {
        Club club = mapper.readValue(req.getInputStream(), Club.class);
        System.out.println(club.toString());

        // 校验数据
        if (club.getName() == null || club.getName().isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be empty");
        }

        return club;
    }

    private void viewClub(HttpServletRequest req, HttpServletResponse resp, Integer ClubId) throws Exception {
        PrintWriter out = resp.getWriter();
        Club club = clubService.getClubById(ClubId);
        if (club != null) {
            out.write("{\"name\":\"" + club.getName() + "\", \"description\":\"" + club.getDescription() + "\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write("{\"error\":\"Club not found.\"}");
        }
    }

    private void listClubs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Club> clubs = clubService.getAllClub();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Use Gson to convert the list to JSON and return it
        Gson gson = new Gson();
        String json = gson.toJson(clubs);
        out.print(json);
        out.flush();
    }

}
