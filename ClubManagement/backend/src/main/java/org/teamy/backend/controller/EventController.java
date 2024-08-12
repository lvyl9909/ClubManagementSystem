package org.teamy.backend.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.DataMapper.EventDataMapper;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.EventService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/events/*")
public class EventController extends HttpServlet {
    EventService eventService;
    @Override
    public void init() throws ServletException {
        // 假设你有一个方法来获取 ClubService 的实例
        // 比如通过依赖注入、服务定位器模式或手动实例化
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
        eventService = new EventService(new EventDataMapper(databaseConnectionManager.getConnection()));  // 假设 ClubMapperImpl 是具体实现
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // 获取URL中的路径部分

        if (pathInfo == null || pathInfo.equals("/")) {
            // /students -> 显示学生列表
            listEvents(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            // /students/{studentId} -> 显示特定学生详情
            Integer eventId = Integer.valueOf(pathInfo.substring(1)); // 去掉前面的斜杠获取ID
            try {
                viewEvent(req, resp, eventId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 返回404错误
        }
    }

    private void viewEvent(HttpServletRequest req, HttpServletResponse resp, Integer eventId) throws Exception {
        PrintWriter out = resp.getWriter();
        Event event = eventService.getEventById(eventId);
        if (event != null) {
            out.write("{\"title\":\"" + event.getTitle() + "\", \"description\":\"" + event.getDescription()+"\", \"club\":\"" + event.getClub() +"\", \"cost\":\"" + event.getCost()+"\", \"venue\":\"" + event.getVenueName()+ "\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write("{\"error\":\"Club not found.\"}");
        }
    }

    private void listEvents(HttpServletRequest req, HttpServletResponse resp) {

    }

}
