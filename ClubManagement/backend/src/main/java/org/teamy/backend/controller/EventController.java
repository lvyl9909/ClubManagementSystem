package org.teamy.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.exception.NotEnoughTicketsException;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.service.EventService;
import org.teamy.backend.service.StudentService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@WebServlet("/student/events/*")
public class EventController extends HttpServlet {
    EventService eventService;
    StudentService studentService;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        eventService = (EventService) getServletContext().getAttribute(ContextListener.EVENT_SERVICE);
        studentService = (StudentService) getServletContext().getAttribute(ContextListener.STUDENT_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);

    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id"); // 获取查询字符串中的 "id" 参数
        String titleParam = req.getParameter("title"); // 获取查询字符串中的 "title" 参数

        RequestHandler handler = () -> {
            if (titleParam != null && !titleParam.trim().isEmpty()) {
                return searchEventsByTitle(titleParam);  // 如果有 title 参数，则进行模糊搜索
            } else if (Objects.equals(idParam, "-1")) {
                return listEvents();  // 如果 id 为 -1，则返回所有事件
            } else {
                Integer eventId = Integer.valueOf(idParam);
                return viewEvent(eventId);  // 根据 ID 获取事件
            }
        };

        MarshallingRequestHandler.of(mapper, resp, handler).handle();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // Gets the path info of the URL
        System.out.println(pathInfo);
        if (pathInfo.equals("/save")) {
            MarshallingRequestHandler.of(
                    mapper, // 使用Jackson的ObjectMapper
                    resp,
                    () -> saveEvent(req)
            ).handle();
        } else if (pathInfo.equals("/applyRSVP")) {
            MarshallingRequestHandler.of(
                    mapper,  // 使用 Jackson 的 ObjectMapper
                    resp,
                    () -> applyForRSVP(req)
            ).handle();
        }else if (pathInfo.equals("/update")) {
            MarshallingRequestHandler.of(
                    mapper,  // 使用 Jackson 的 ObjectMapper
                    resp,
                    () -> updateEvent(req)
            ).handle();
        }else if (pathInfo.equals("/delete")) {
            MarshallingRequestHandler.of(
                    mapper,
                    resp,
                    () -> deleteEvent(req)
            ).handle();
        }
    }


    private ResponseEntity deleteEvent(HttpServletRequest req) {
        try {
            Map<String, List<Integer>> requestBody = mapper.readValue(req.getInputStream(), new TypeReference<Map<String, List<Integer>>>() {});
            List<Integer> eventsIds = requestBody.get("eventsIds");
            System.out.println(eventsIds);
            // 调用删除事件的方法
            eventService.deleteEvent(eventsIds);
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

    private ResponseEntity updateEvent(HttpServletRequest req) {
        try {
            // 从请求体中解析 Event 对象
            Event event = mapper.readValue(req.getInputStream(), Event.class);

            // 调用更新事件的方法
            boolean isUpdated = eventService.updateEvent(event);

            if (isUpdated) {
                return ResponseEntity.ok(null);
            } else {
                return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                        Error.builder()
                                .status(HttpServletResponse.SC_BAD_REQUEST)
                                .message("Failed to update the event.")
                                .reason("Event update operation failed.")
                                .build()
                );
            }

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
                            .message("An error occurred while updating the event.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }

    private ResponseEntity applyForRSVP(HttpServletRequest req) {
        try {
            // 从请求中解析必要的参数
            BufferedReader reader = req.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String json = jsonBuilder.toString();

            Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

            int eventId = (Integer) jsonMap.get("eventId");
            int numTickets = (Integer) jsonMap.get("numTickets");

            List<Integer> participatesId = (List<Integer>) jsonMap.get("participants_id");
            System.out.println("participant:"+participatesId);
            eventService.applyForRSVP(eventId, Math.toIntExact(studentService.getCurrentStudent().getId()), numTickets, participatesId);
            return ResponseEntity.ok(null); // 成功返回空响应
        } catch (NotEnoughTicketsException e) {
            return ResponseEntity.of(HttpServletResponse.SC_BAD_REQUEST,
                    Error.builder()
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .message("Not enough tickets available.")
                            .reason(e.getMessage())
                            .build());
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
                            .message("An error occurred while applying RSVP.")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }


    private ResponseEntity searchEventsByTitle(String title) {
        try {
            List<Event> events = eventService.getEventByTitle(title);
            return ResponseEntity.of(HttpServletResponse.SC_OK, events);
        } catch (Exception e) {
            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Error.builder()
                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .message("Failed to search events by title")
                            .reason(e.getMessage())
                            .build()
            );
        }
    }

    private Event parseEventFromRequest(HttpServletRequest req) throws IOException {
        Event event = mapper.readValue(req.getInputStream(), Event.class);
        System.out.println(event.toString());

        if ( event.getTitle().isEmpty()||event.getTitle() == null ) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        return event;
    }
    private ResponseEntity viewEvent(Integer eventId) {
        Event event = null;
        try {
            event = eventService.getEventById(eventId);
            Integer currentCapacity = eventService.getCurrentCapacity(event);
            event.setCurrentCapacity(currentCapacity);
            return ResponseEntity.ok(event);
        }catch (NumberFormatException e) {
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
    private ResponseEntity listEvents() {

        List<Event> events = null;
        try {
            events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Error.builder()
                            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .message("error")
                            .reason(e.getMessage())
                            .build());
        }
    }
    private ResponseEntity saveEvent(HttpServletRequest req) {
        try {
            // 解析请求体中的Club数据，假设请求体是JSON格式
            Event event = parseEventFromRequest(req);

            // 调用Service层保存Club
            boolean isSaved = eventService.saveEvent(event);

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

}
