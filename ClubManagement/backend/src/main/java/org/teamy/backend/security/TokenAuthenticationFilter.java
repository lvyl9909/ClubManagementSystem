package org.teamy.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.teamy.backend.security.repository.TokenService;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String TOKEN_TYPE_BEARER = "Bearer";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final Pattern PATTERN_TOKEN = Pattern.compile("^" + TOKEN_TYPE_BEARER + " (.*)$");

    private final TokenService jwtTokenService;

    protected TokenAuthenticationFilter(RequestMatcher authenticationRequired, TokenService jwtTokenService) {
        super(authenticationRequired);
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        var authorizationHeader = Optional.ofNullable(request.getHeader(HEADER_AUTHORIZATION))
                .orElseThrow(() -> new BadCredentialsException(String.format("%s header is required", HEADER_AUTHORIZATION)));
        var matcher = PATTERN_TOKEN.matcher(authorizationHeader);
        if (matcher.find()) {
            var token = matcher.group(1);
            var authentication = jwtTokenService.readToken(token);
            try {
                var result = getAuthenticationManager().authenticate(authentication); // 验证对象
                return result;
            } catch (AuthenticationException e) {
                System.out.println("Authentication failed: " + e.getMessage());
                throw e;
            }
        }
        throw new BadCredentialsException(String.format("invalid %s header value", HEADER_AUTHORIZATION));
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        System.out.println("User authenticated: " + authResult.getName());
        System.out.println("Authorities: " + authResult.getAuthorities().toString());
        chain.doFilter(request, response);
    }
}
