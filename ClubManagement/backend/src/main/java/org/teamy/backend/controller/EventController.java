package org.teamy.backend.controller;

import com.google.gson.Gson;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

@WebServlet("/events/*")
public class EventController extends HttpServlet {
    EventService eventService;
    private Gson gson = new Gson();  // Gson 实例

    @Override
    public void init() throws ServletException {
        // 假设你有一个方法来获取 ClubService 的实例
        // 比如通过依赖注入、服务定位器模式或手动实例化
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
        eventService = new EventService(new EventDataMapper(databaseConnectionManager.getConnection()));  // 假设 ClubMapperImpl 是具体实现
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id"); // 从查询字符串中获取 "id" 参数
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        String pathInfo = req.getPathInfo(); // 获取URL中的路径部分

        if (Objects.equals(idParam, "-1")) {
            listEvents(req, resp); // 如果没有 id 参数，则列出所有俱乐部
        } else {
            try {
                Integer clubId = Integer.valueOf(idParam); // 将 id 参数转换为整数
                viewEvent(req, resp, clubId); // 调用 viewClub 方法
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
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        System.out.println("processing");
        String pathInfo = req.getPathInfo(); // 获取URL中的路径部分

        if (pathInfo.equals("/save")) {
            saveEvent(req, resp);
        }
    }

    private void saveEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 从请求中解析event数据，假设请求体为JSON格式
            Event event = parseEventFromRequest(req);

            // 调用Service层保存event
            boolean isSaved = eventService.saveEvent(event);

            if (isSaved) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("Event saved successfully.");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Failed to save the event.");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An error occurred while saving the event. "+e.getMessage());
        }
    }

    private Event parseEventFromRequest(HttpServletRequest req) throws IOException {
        // 使用BufferedReader读取请求体
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuffer.append(line);
            System.out.println(line);
        }

        // 使用Gson将JSON字符串解析为Java对象
        Event event = gson.fromJson(jsonBuffer.toString(), Event.class);
        System.out.println(event.toString());

        // 校验数据
        if (event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }

        return event;
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

    private void listEvents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Event> clubs = eventService.getAllEvents();
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
