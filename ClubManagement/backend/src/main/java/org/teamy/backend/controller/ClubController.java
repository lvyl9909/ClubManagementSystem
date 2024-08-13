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

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/clubs/*")
public class ClubController extends HttpServlet {
    private ClubService clubService;
    @Override
    public void init() throws ServletException {
        // 假设你有一个方法来获取 ClubService 的实例
        // 比如通过依赖注入、服务定位器模式或手动实例化
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
        clubService = new ClubService(new ClubDataMapper(databaseConnectionManager.getConnection()));  // 假设 ClubMapperImpl 是具体实现
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // 获取URL中的路径部分

        if (pathInfo == null || pathInfo.equals("/")) {
            // /students -> 显示学生列表
            listClubs(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            // /students/{studentId} -> 显示特定学生详情
            Integer studentId = Integer.valueOf(pathInfo.substring(1)); // 去掉前面的斜杠获取ID
            try {
                viewClub(req, resp, studentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 返回404错误
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            resp.getWriter().write("An error occurred while saving the club.");
        }
    }

    private Club parseClubFromRequest(HttpServletRequest req) {
        // 假设请求体为JSON格式，可以使用第三方库如Jackson来解析JSON
        // 这里是一个简化的示例，实际代码应该包括完整的解析和错误处理逻辑
        String name = req.getParameter("name");
        String description = req.getParameter("description");

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be empty");
        }

        Club club = new Club();
        club.setName(name);
        club.setDescription(description);

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

    private void listClubs(HttpServletRequest req, HttpServletResponse resp) {
    }

}
