package org.teamy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.Venue;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RequestHandler;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.service.FundingApplicationService;
import org.teamy.backend.service.VenueService;

import java.io.IOException;
import java.util.List;

@WebServlet("/student/venues/*")
public class VenueController extends HttpServlet {
    VenueService venueService;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        venueService = (VenueService) getServletContext().getAttribute(ContextListener.VENUE_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RequestHandler handler = () -> {
            return viewAllVenue();  // 根据 ID 获取事件
        };

        MarshallingRequestHandler.of(mapper, resp, handler).handle();
    }

    private ResponseEntity viewAllVenue() {
        try {
            List<Venue> venues = venueService.getAllVenue();
            return ResponseEntity.ok(venues);
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
}
