package org.teamy.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/auth/logout")
public class LogoutResource extends HttpServlet {

    private static final String COOKIE_NAME_REFRESH_TOKEN = "RefreshToken";
    private static final String PATH_AUTH_TOKEN = "/auth/token";

    // 配置参数（可以通过依赖注入或读取配置文件获得）
    private final boolean secureCookies = true;
    private final String domain = "localhost";  // 替换为实际域名

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 处理用户注销逻辑（例如：从服务器端注销用户会话）

        // 删除刷新令牌的Cookie
        removeRefreshTokenCookie(resp, req.getContextPath());

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void removeRefreshTokenCookie(HttpServletResponse response, String contextPath) {
        Cookie cookie = new Cookie(COOKIE_NAME_REFRESH_TOKEN, null);
        cookie.setSecure(secureCookies);
        cookie.setMaxAge(0);  // 设置MaxAge为0以删除Cookie
        cookie.setHttpOnly(true);
        cookie.setDomain(domain);
        cookie.setPath(contextPath + PATH_AUTH_TOKEN);
        response.addCookie(cookie);
    }
}