package org.teamy.backend.security.repository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.teamy.backend.security.model.RefreshToken;
import org.teamy.backend.security.model.Token;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtTokenServiceImpl implements TokenService{
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_AUTHORITIES = "authorities";
    private final RefreshTokenRepository repository;
    private final long timeToLiveSeconds;
    private final String issuer;
    private final String secret;
    private SecretKey key;

    public JwtTokenServiceImpl(String key,Integer timeToLiveSeconds, String issuer,RefreshTokenRepository repository) {
        this.secret = key;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.repository = repository;
        this.issuer = issuer;
    }

    @Override
    public UsernamePasswordAuthenticationToken readToken(String accessToken) {
        try {
            var jws = parse(accessToken);
            return new UsernamePasswordAuthenticationToken(getUsername(jws.getBody()), null, getAuthorities(jws.getBody()));
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("token expired");
        } catch (JwtException e) {
            throw new BadCredentialsException("bad token");
        }
    }
    @Override
    public Token createToken(UserDetails user) {
        return generateToken(user.getUsername(), user.getAuthorities());
    }

    @Override
    public Token refresh(String accessToken, String refreshTokenId) {
        var claims = parseExpired(accessToken);
        var refresh = repository.get(refreshTokenId)
                .orElseThrow(() -> new BadCredentialsException("bad refresh token"));
        if (!refresh.getTokenId().equals(claims.getId())) {
            throw new BadCredentialsException("bad refresh token");
        }
        repository.delete(refreshTokenId);
        return generateToken(getUsername(claims), getAuthorities(claims));
    }

    @Override
    public void logout(String username) {
        repository.deleteAllForUsername(username);
    }


    private Token generateToken(String username, Collection<? extends GrantedAuthority> authorities) {

        var now = new Date();
        var expires = Date.from(now.toInstant().plusSeconds(timeToLiveSeconds));
        var id = UUID.randomUUID().toString();
        var tokenStr = Jwts.builder()
                .setIssuer(issuer)
                .setSubject("swen90007")
                .setAudience(issuer)
                .setExpiration(expires)
                .setNotBefore(now)
                .setIssuedAt(now)
                .setId(id)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_AUTHORITIES, authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .signWith(getKey())
                .compact();

        var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setTokenId(id);
        refreshToken.setUsername(username);
        repository.save(refreshToken);

        var token = new Token();
        token.setAccessToken(tokenStr);
        token.setRefreshTokenId(refreshToken.getId());
        return token;
    }
    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .requireAudience(this.issuer)
                .build()
                .parseClaimsJws(token);
    }

    private SecretKey getKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }
    private Claims parseExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .requireAudience(this.issuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 返回过期的JWT Claims
            return e.getClaims();
        }
    }
    private String getUsername(Claims claims) {
        return claims.get(CLAIM_USERNAME, String.class);
    }
    private Collection<? extends GrantedAuthority> getAuthorities(Claims claims) {
        List<String> authorities = claims.get(CLAIM_AUTHORITIES, List.class);
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
