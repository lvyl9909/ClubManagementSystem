package org.teamy.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.*;
import org.teamy.backend.security.model.Token;
import org.teamy.backend.security.repository.JwtTokenServiceImpl;
import org.teamy.backend.security.repository.PostgresRefreshTokenRepository;
import org.teamy.backend.security.repository.TokenService;
import com.google.gson.Gson;


import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;

@WebServlet("/auth/token")
public class TokenResource extends HttpServlet {

    public static final String PATH_AUTH_TOKEN = "/auth/token";
    private static final String COOKIE_NAME_REFRESH_TOKEN = "refreshToken";
    private ObjectMapper mapper;
    private TokenService jwtTokenService;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private boolean secureCookies;
    private String domain;
    private int cookieTimeToLiveSeconds;


    @Override
    public void init() throws ServletException {
        super.init();
        jwtTokenService = (TokenService) getServletContext().getAttribute(ContextListener.TOKEN_SERVICE);
        userDetailsService = (UserDetailsService) getServletContext().getAttribute(ContextListener.USER_DETAILS_SERVICE);
        passwordEncoder = (PasswordEncoder) getServletContext().getAttribute(ContextListener.PASSWORD_ENCODER);
        mapper = (ObjectMapper) getServletContext().getAttribute(ContextListener.MAPPER);
        domain = (String) getServletContext().getAttribute(ContextListener.DOMAIN);
        secureCookies = (boolean) getServletContext().getAttribute(ContextListener.SECURE_COOKIES);
        cookieTimeToLiveSeconds = (int) getServletContext().getAttribute(ContextListener.COOKIE_TIME_TO_LIVE_SECONDS);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarshallingRequestHandler.of(mapper, resp, ErrorHandler.of(() -> {
            try {
                var bodyBuffer = new StringWriter();
                req.getReader().transferTo(bodyBuffer);
                var login = mapper.readValue(bodyBuffer.toString(), LoginRequest.class);
                UserDetails userDetails = Optional.ofNullable(userDetailsService.loadUserByUsername(login.getUsername()))
                        .orElseThrow(ForbiddenException::new);
                if (!passwordEncoder.matches(login.getPassword(), userDetails.getPassword())) {
                    throw new ForbiddenException();
                }
                var token = jwtTokenService.createToken(userDetails);
                resp.addCookie(refreshCookie(token.getRefreshTokenId(), req.getContextPath()));
                return tokenResponse(token.getAccessToken());
            } catch (IOException e) {
                throw new ValidationException(String.format("invalid token body: %s", e.getMessage()));
            }
        })).handle();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarshallingRequestHandler.of(mapper, resp, ErrorHandler.of(() -> {
            try {
                var bodyBuffer = new StringWriter();
                req.getReader().transferTo(bodyBuffer);
                var refreshRequest = mapper.readValue(bodyBuffer.toString(), RefreshRequest.class);

                var refreshToken = getRefreshCookie(req);
                var token = jwtTokenService.refresh(refreshRequest.getAccessToken(), refreshToken);
                resp.addCookie(refreshCookie(token.getRefreshTokenId(), req.getContextPath()));
                return tokenResponse(token.getAccessToken());
            } catch (IOException e) {
                throw new ValidationException(String.format("invalid token body: %s", e.getMessage()));
            }
        })).handle();
    }

    private Cookie refreshCookie(String refreshToken, String contextPath) {
        var cookie = new Cookie(COOKIE_NAME_REFRESH_TOKEN, refreshToken);
        cookie.setSecure(secureCookies);
        cookie.setMaxAge(cookieTimeToLiveSeconds);
        cookie.setHttpOnly(true);
        cookie.setDomain(domain);
        cookie.setPath(contextPath + PATH_AUTH_TOKEN);
        return cookie;
    }

    private ResponseEntity tokenResponse(String accessToken) {
        var token = new Token();
        token.setAccessToken(accessToken);
        token.setType(TokenAuthenticationFilter.TOKEN_TYPE_BEARER);
        return ResponseEntity.ok(token);
    }

    private String getRefreshCookie(HttpServletRequest req) {
        return Arrays.stream(Optional.ofNullable(req.getCookies()).orElse(new Cookie[]{}))
                .filter(c -> c.getName().equals(COOKIE_NAME_REFRESH_TOKEN))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new BadCredentialsException("no refresh cookie set"));
    }


}