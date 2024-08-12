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

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 返回404错误
        }
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
