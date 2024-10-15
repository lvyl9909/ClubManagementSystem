package org.teamy.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.model.exception.Error;
import org.teamy.backend.model.exception.ErrorHandler;
import org.teamy.backend.model.exception.ForbiddenException;
import org.teamy.backend.model.exception.ValidationException;
import org.teamy.backend.model.request.LoginRequest;
import org.teamy.backend.model.request.MarshallingRequestHandler;
import org.teamy.backend.model.request.RefreshRequest;
import org.teamy.backend.model.request.ResponseEntity;
import org.teamy.backend.security.model.Token;
import org.teamy.backend.security.repository.TokenService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                System.out.println("doPost token start");
                System.out.println(login.getUsername());
                UserDetails userDetails = Optional.ofNullable(userDetailsService.loadUserByUsername(login.getUsername()))
                        .orElseThrow(ForbiddenException::new);
                for (GrantedAuthority authority : userDetails.getAuthorities()) {
                    System.out.println(authority.getAuthority());

                }

                System.out.println(login.getPassword());
                System.out.println(userDetails.getPassword());

                if (!login.getPassword().equals(userDetails.getPassword())) {
                    throw new ForbiddenException();
                }
                // 校验密码成功后，创建 Authentication 对象并设置到 SecurityContextHolder 中
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("password correct");
                var token = jwtTokenService.createToken(userDetails);
                System.out.println("token created");
                Cookie refreshCookie = refreshCookie(token.getRefreshTokenId(), req.getContextPath());
                addSameSiteCookie(resp, refreshCookie, "None");
//                resp.addCookie(refreshCookie);
                System.out.println("Cookie added");



                return tokenResponse(token.getAccessToken());
            }catch (UsernameNotFoundException e) {
                return ResponseEntity.of(HttpServletResponse.SC_UNAUTHORIZED, Error.builder()
                        .status(HttpServletResponse.SC_UNAUTHORIZED)
                        .message("UsernameNotFound")
                        .reason(e.getMessage())
                        .build());
            } catch (ForbiddenException e) {
                return ResponseEntity.of(HttpServletResponse.SC_FORBIDDEN,Error.builder()
                        .status(HttpServletResponse.SC_FORBIDDEN)
                        .message("Forbidden")
                        .reason(e.getMessage())
                        .build());
            } catch (Exception e) {
                return ResponseEntity.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
            }
        })).handle();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarshallingRequestHandler.of(mapper, resp, ErrorHandler.of(() -> {
            try {
                var bodyBuffer = new StringWriter();
                req.getReader().transferTo(bodyBuffer);
                System.out.println("doPut Cookie start");

                var refreshRequest = mapper.readValue(bodyBuffer.toString(), RefreshRequest.class);
                Cookie[] cookies = req.getCookies();
                if (cookies == null) {
                    // no cookies in the request
                    System.out.println("No cookies in request");
                } else {
                    System.out.println("Cookies count: " + cookies.length);
                    Arrays.stream(cookies).forEach(cookie ->
                            System.out.println("Cookie Name: " + cookie.getName() + ", Cookie Value: " + cookie.getValue()));
                }
                var refreshToken = getRefreshCookie(req);
                System.out.println(refreshToken);
                var token = jwtTokenService.refresh(refreshRequest.getAccessToken(), refreshToken);
                System.out.println("3");

                Cookie refreshCookie = refreshCookie(token.getRefreshTokenId(), req.getContextPath());
                addSameSiteCookie(resp, refreshCookie, "None");

                System.out.println("4");

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

    private void addSameSiteCookie(HttpServletResponse resp, Cookie cookie, String sameSite) {
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
        cookieBuilder.append(" Path=").append(cookie.getPath()).append(";");
        if (cookie.getDomain() != null) {
            cookieBuilder.append(" Domain=").append(cookie.getDomain()).append(";");
        }
        if (cookie.getMaxAge() > 0) {
            cookieBuilder.append(" Max-Age=").append(cookie.getMaxAge()).append(";");
        }
        if (cookie.getSecure()) {
            cookieBuilder.append(" Secure;");
        }
        if (cookie.isHttpOnly()) {
            cookieBuilder.append(" HttpOnly;");
        }
        cookieBuilder.append(" SameSite=").append(sameSite).append(";");

        resp.addHeader("Set-Cookie", cookieBuilder.toString());
    }

    private ResponseEntity tokenResponse(String accessToken) {
        var token = new Token();
        token.setAccessToken(accessToken);
        token.setType(TokenAuthenticationFilter.TOKEN_TYPE_BEARER);
        return ResponseEntity.ok(token);
    }

    private String getRefreshCookie(HttpServletRequest req) {
        Cookie[] cookies = Optional.ofNullable(req.getCookies()).orElse(new Cookie[]{});
        System.out.println("Total cookies: " + cookies.length);

        // Step 2: 打印每个 cookie 的名称和值
        Arrays.stream(cookies).forEach(cookie ->
                System.out.println("Cookie Name: " + cookie.getName() + ", Cookie Value: " + cookie.getValue())
        );

        // Step 3: 查找特定的 refresh token cookie
        return Arrays.stream(cookies)
                .filter(c -> {
                    boolean isMatch = c.getName().equals(COOKIE_NAME_REFRESH_TOKEN);
                    System.out.println("Checking cookie: " + c.getName() + ", Match: " + isMatch);
                    return isMatch;
                })
                .findFirst()
                .map(cookie -> {
                    System.out.println("Found refresh token cookie: " + cookie.getValue());
                    return cookie.getValue();
                })
                .orElseThrow(() -> {
                    System.out.println("No refresh token cookie found");
                    return new BadCredentialsException("No refresh cookie set");
                });
    }


}