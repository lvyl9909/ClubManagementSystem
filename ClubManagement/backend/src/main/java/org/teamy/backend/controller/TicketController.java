package org.teamy.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.StudentService;
import org.teamy.backend.service.TicketService;

import java.io.IOException;
import java.util.List;

@WebServlet("/student/tickets/*")
public class TicketController extends HttpServlet {
    TicketService ticketService;
    private ObjectMapper mapper;
    @Override
    public void init() throws ServletException {
        ticketService = (TicketService) getServletContext().getAttribute(ContextListener.TICKET_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
        System.out.println("success init");
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        System.out.println(pathInfo);
        if (pathInfo != null && pathInfo.equals("/delete/")) {
            MarshallingRequestHandler.of(
                    mapper, // 使用Jackson的ObjectMapper
                    resp,
                    () -> deleteTicket(req)
            ).handle();
        }
    }

    private ResponseEntity deleteTicket(HttpServletRequest req) {
        try {
            String idParam = req.getParameter("id"); // 获取查询字符串中的 "id" 参数
            System.out.println("deleteid:"+idParam);
            // 调用删除事件的方法
            if (idParam == null) {
                throw new IllegalArgumentException("Missing 'id' parameter.");
            }

            ticketService.deleteTicket(Integer.valueOf(idParam));
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
}
