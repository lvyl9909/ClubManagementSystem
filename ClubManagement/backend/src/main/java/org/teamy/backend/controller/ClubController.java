package org.teamy.backend.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.service.ClubService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;


@WebServlet("/clubs/*")
public class ClubController extends HttpServlet {
    private ClubService clubService;
    private Gson gson = new Gson();  // Gson 实例

    @Override
    public void init() throws ServletException {
        // 假设你有一个方法来获取 ClubService 的实例
        // 比如通过依赖注入、服务定位器模式或手动实例化
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
        clubService = new ClubService(new ClubDataMapper(databaseConnectionManager.getConnection()));  // 假设 ClubMapperImpl 是具体实现
        System.out.println("success init");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id"); // 从查询字符串中获取 "id" 参数
        String pathInfo = req.getPathInfo(); // 获取URL中的路径部分

        if (Objects.equals(idParam, "-1")) {
            listClubs(req, resp); // 如果没有 id 参数，则列出所有俱乐部
        } else {
            try {
                Integer clubId = Integer.valueOf(idParam); // 将 id 参数转换为整数
                viewClub(req, resp, clubId); // 调用 viewClub 方法
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format"); // 返回400错误，说明ID格式无效
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
//        else {
//            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 返回404错误
//        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("processing");
        String pathInfo = req.getPathInfo(); // 获取URL中的路径部分

        if (pathInfo.equals("/save")) {
            saveClub(req, resp);
        }
    }

    private void saveClub(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 从请求中解析俱乐部数据，假设请求体为JSON格式
            Club club = parseClubFromRequest(req);

            // 调用Service层保存俱乐部
            boolean isSaved = clubService.saveClub(club);

            if (isSaved) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("Club saved successfully.");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Failed to save the club.");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An error occurred while saving the club. "+e.getMessage());
        }
    }

    private Club parseClubFromRequest(HttpServletRequest req) throws IOException {
        // 使用BufferedReader读取请求体
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuffer.append(line);
            System.out.println(line);
        }

        // 使用Gson将JSON字符串解析为Java对象
        Club club = gson.fromJson(jsonBuffer.toString(), Club.class);
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

        // 使用Gson将列表转换为JSON并返回
        Gson gson = new Gson();
        String json = gson.toJson(clubs);
        out.print(json);
        out.flush();
    }

}
