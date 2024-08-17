package org.teamy.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.security.repository.JwtTokenServiceImpl;
import org.teamy.backend.security.repository.PostgresRefreshTokenRepository;

import java.io.IOException;

@WebServlet("/auth/token")
public class TokenResource extends HttpServlet {

    private static final String COOKIE_NAME_REFRESH_TOKEN = "RefreshToken";
    private static final String PATH_AUTH_TOKEN = "/auth/token";
    private PostgresRefreshTokenRepository postgresRefreshTokenRepository;
    private JwtTokenServiceImpl jwtTokenService;

    // 配置参数（可以通过依赖注入或读取配置文件获得）
    private final boolean secureCookies = true;
    private final int cookieTimeToLiveSeconds = 7 * 24 * 60 * 60;  // 7天
    private final String domain = "localhost";  // 替换为实际域名
    private final Long SECOND_PER_HOUR = 3600L;
    private final String ISSUER = "TeamY";

    @Override
    public void init() throws ServletException {
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
        this.postgresRefreshTokenRepository = new PostgresRefreshTokenRepository(databaseConnectionManager.getConnection());
        this.jwtTokenService = new JwtTokenServiceImpl(postgresRefreshTokenRepository, 2 *SECOND_PER_HOUR,ISSUER);
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