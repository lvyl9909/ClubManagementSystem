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
import org.teamy.backend.model.Club;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.model.request.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;


@WebServlet("/student/clubs/*")
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 获取用户的角色权限
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            System.out.println(authorities);

            // 定义请求处理逻辑
            RequestHandler handler = () -> {
                if (Objects.equals(idParam, "-1")) {
                    // 允许访问俱乐部列表
                    return listClubs();
                } else {
                    try {
                        HashMap<Integer,Integer> map = new HashMap<>();
                        int[] nums = new int[0];
                        map.getOrDefault(nums[0],0);
                        Integer clubId = Integer.valueOf(idParam); // 将id参数转换为整数

                        // 检查用户是否拥有访问该 clubId 的权限
                        if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_CLUB_" + clubId))) {
                            // 用户有权限访问该 club，继续处理
                            return viewClub(clubId);
                        } else {
                            // 用户没有访问该 club 的权限，返回 403 Forbidden
                            return ResponseEntity.of(HttpServletResponse.SC_FORBIDDEN,
                                    Error.builder()
                                            .status(HttpServletResponse.SC_FORBIDDEN)
                                            .message("Access Denied")
                                            .reason("You do not have permission to access this club.")
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

            // 执行请求处理逻辑
            MarshallingRequestHandler.of(mapper, resp, handler).handle();
        } else {
            // 如果用户未认证，返回 401 Unauthorized
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to access this resource.");
        }

//        RequestHandler handler = () -> {
//            if (Objects.equals(idParam, "-1")) {
//                return listClubs();
//            } else {
//                try {
//                    Integer clubId = Integer.valueOf(idParam); // 将id参数转换为整数
//                    return viewClub(clubId);
//                } catch (NumberFormatException e) {
//                    return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
//                            Error.builder()
//                                    .status(HttpServletResponse.SC_BAD_REQUEST)
//                                    .message("Invalid ID format")
//                                    .reason(e.getMessage())
//                                    .build()
//                    );
//                } catch (Exception e) {
//                    return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//                            Error.builder()
//                                    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
//                                    .message("error")
//                                    .reason(e.getMessage())
//                                    .build()
//                    );
//                }
//            }
//        };
//
//        MarshallingRequestHandler.of(mapper, resp, handler).handle();
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
                            System.out.println(club.toString());
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
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        System.out.println("Received JSON: " + sb.toString());
        Club club = mapper.readValue(req.getInputStream(), Club.class);

        // 校验数据
        if (club.getName() == null || club.getName().isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be empty");
        }
        return club;
    }
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
