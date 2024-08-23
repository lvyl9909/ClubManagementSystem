package org.teamy.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.*;
import org.teamy.backend.model.exception.ErrorHandler;
import org.teamy.backend.model.request.LogoutRequest;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.security.repository.TokenService;

import java.io.IOException;
import java.io.StringWriter;

@WebServlet("/auth/logout")
public class LogoutResource extends HttpServlet {
    private ObjectMapper mapper;
    private TokenService jwtTokenService;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        MarshallingRequestHandler.of(mapper, resp, ErrorHandler.of(() -> {
            try {
                var bodyBuffer = new StringWriter();
                req.getReader().transferTo(bodyBuffer);
                var logout = mapper.readValue(bodyBuffer.toString(), LogoutRequest.class);
                jwtTokenService.logout(logout.getUsername());
                return ResponseEntity.ok(null);
            } catch (IOException e) {
                throw new ValidationException(String.format("invalid logout body: %s", e.getMessage()));
            }
        })).handle();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        jwtTokenService = (TokenService) getServletContext().getAttribute(ContextListener.TOKEN_SERVICE);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
    }
}