package org.teamy.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.security.repository.JwtTokenServiceImpl;
import org.teamy.backend.security.repository.PostgresRefreshTokenRepository;
import org.teamy.backend.security.repository.TokenService;
import com.google.gson.Gson;


import java.io.IOException;

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
        // 处理用户登录逻辑（验证用户名和密码）

        // 如果验证成功，生成访问令牌和刷新令牌
        String refreshToken = generateRefreshToken();  // 生成刷新令牌
        String contextPath = req.getContextPath();

        // 创建并添加刷新令牌的Cookie
        Cookie refreshTokenCookie = refreshCookie(refreshToken, contextPath);
        resp.addCookie(refreshTokenCookie);

        // 返回访问令牌作为响应
        String accessToken = generateAccessToken();  // 生成访问令牌
        resp.getWriter().write(accessToken);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 从请求中提取刷新令牌
        String refreshToken = extractRefreshToken(req);
        if (refreshToken == null || !isValidRefreshToken(refreshToken)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 使用刷新令牌生成新的访问令牌
        String newAccessToken = generateAccessToken();  // 生成新的访问令牌
        resp.getWriter().write(newAccessToken);
        resp.setStatus(HttpServletResponse.SC_OK);
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

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME_REFRESH_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 以下是用于生成令牌的伪方法，实际实现需要根据你的需求定制
    private String generateRefreshToken() {
        // 生成刷新令牌的逻辑
        return "sample-refresh-token";
    }

    private String generateAccessToken() {
        // 生成访问令牌的逻辑
        return "sample-access-token";
    }

    private boolean isValidRefreshToken(String refreshToken) {
        // 验证刷新令牌的逻辑
        return true;  // 假设验证成功
    }
}