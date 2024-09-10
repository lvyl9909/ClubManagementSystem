package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.DTO.TicketEventDTO;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.*;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.ClubService;
import org.teamy.backend.service.StudentService;
import org.teamy.backend.service.TicketService;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/student/userdetailed/*")
public class UserDetailedController extends HttpServlet {
    StudentService studentService;
    TicketService ticketService;
    ClubService clubService;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        clubService = (ClubService) getServletContext().getAttribute(ContextListener.CLUB_SERVICE);
        studentService = (StudentService) getServletContext().getAttribute(ContextListener.STUDENT_SERVICE);
        ticketService = (TicketService) getServletContext().getAttribute(ContextListener.TICKET_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
        System.out.println("success init");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        String pathInfo = req.getPathInfo(); // Gets the path info of the URL

        RequestHandler handler = () -> {
            if (pathInfo.equals("/club")) {
                return listClubs();
            }else if (pathInfo.equals("/info")) {
                return viewStudent();
            }else if (pathInfo.equals("/tickets")) {
                return listTickets();
            }else if (pathInfo.equals("/rsvp")) {
                return listRSVP();
            }
            return ResponseEntity.of(HttpServletResponse.SC_NOT_FOUND,
                    Error.builder()
                            .status(HttpServletResponse.SC_NOT_FOUND)
                            .message("page not found")
                            .reason("page not found")
                            .build()
            );        };
        MarshallingRequestHandler.of(mapper, resp, handler).handle();
    }

    private ResponseEntity listClubs() {
//        Integer currentStudent = studentService.getCurrentStudent();
        List<Club> clubs = studentService.getClub(Math.toIntExact(studentService.getCurrentStudent().getId()));
        return ResponseEntity.ok(clubs);
    }
    private ResponseEntity listTickets() {
        Map<Ticket, Event> ticketInfo;
        try {
            System.out.println("current student id:" + studentService.getCurrentStudent().getId());
            ticketInfo = ticketService.getTicketInfo(studentService.getCurrentStudent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(convertMapToDTOList(ticketInfo)); // 转换为DTO后返回
    }
    public List<TicketEventDTO> convertMapToDTOList(Map<Ticket, Event> ticketInfo) {
        List<TicketEventDTO> dtoList = new ArrayList<>();
        for (Map.Entry<Ticket, Event> entry : ticketInfo.entrySet()) {
            Ticket ticket = entry.getKey();
            Event event = entry.getValue();
            TicketEventDTO dto = new TicketEventDTO(ticket, event);
            dtoList.add(dto);
        }
        return dtoList;
    }
    private ResponseEntity listRSVP() {
        List<RSVP> rsvps = studentService.getRSVP(studentService.getCurrentStudent());
        return ResponseEntity.ok(rsvps);
    }
    private ResponseEntity viewStudent() {
        Student student = studentService.getCurrentStudent();
        return ResponseEntity.ok(student);
    }
}
