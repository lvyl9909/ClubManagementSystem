package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.model.request.ResponseEntity;

import java.io.IOException;
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

        String idParam = req.getParameter("id"); // 获取查询字符串中的 "id" 参数

        RequestHandler handler = () -> {
            if (Objects.equals(idParam, "-1")) {
                return listClubs();
            } else {
                try {
                    Integer clubId = Integer.valueOf(idParam); // 将id参数转换为整数
                    return viewClub(clubId);
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
        };

        MarshallingRequestHandler.of(mapper, resp, handler).handle();
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
                            clubService.saveClub(club);
                            return ResponseEntity.create(club);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                                    Error.builder()
                                            .status(HttpServletResponse.SC_BAD_REQUEST)
                                            .message("Failed to save the club.")
                                            .reason(e.getMessage())
                                            .build()
                            );
                        } catch (RuntimeException e){
                            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                                    Error.builder()
                                            .status(HttpServletResponse.SC_BAD_REQUEST)
                                            .message("Failed to save the club.")
                                            .reason("Failed to save the club.")
                                            .build()
                            );
                        }
                        catch (Exception e) {
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

//    private void viewClub(HttpServletRequest req, HttpServletResponse resp, Integer ClubId) throws Exception {
//        PrintWriter out = resp.getWriter();
//        Club club = clubService.getClubById(ClubId);
//        if (club != null) {
//            out.write("{\"name\":\"" + club.getName() + "\", \"description\":\"" + club.getDescription() + "\"}");
//        } else {
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            out.write("{\"error\":\"Club not found.\"}");
//        }
//    }

//    private void listClubs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        List<Club> clubs = clubService.getAllClub();
//        resp.setContentType("application/json");
//        resp.setCharacterEncoding("UTF-8");
//        PrintWriter out = resp.getWriter();
//
//        // Use Gson to convert the list to JSON and return it
//        Gson gson = new Gson();
//        String json = gson.toJson(clubs);
//        out.print(json);
//        out.flush();
//    }
    private ResponseEntity listClubs() {
        List<Club> clubs = clubService.getAllClub();
        return ResponseEntity.ok(clubs);
    }
    private ResponseEntity viewClub(Integer clubId) throws Exception {
        Club club = clubService.getClubById(clubId);
        if (club != null) {
            return ResponseEntity.ok(club);
        } else {
            return ResponseEntity.of(HttpServletResponse.SC_NOT_FOUND,
                    Error.builder()
                            .status(HttpServletResponse.SC_NOT_FOUND)
                            .message("Club not found.")
                            .reason("Club not found.")
                            .build()
            );
        }
    }


}
