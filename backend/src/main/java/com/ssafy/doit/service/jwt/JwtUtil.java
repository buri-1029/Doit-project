package com.ssafy.doit.service.jwt;

import com.ssafy.doit.model.user.User;
import com.ssafy.doit.service.user.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    public final static long TOKEN_VALIDATION_SECOND = 1000L * 60;
    public final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 24;

    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";

    @Value("${spring.jwt.secret}")
    private String SECRET_KEY;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUser(String token) {
        return extractAllClaims(token).get("user", String.class);
    }

    public Boolean isTokenExpired(String token) throws ExpiredJwtException{
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public String generateToken(String user) {
        return doGenerateToken(user, TOKEN_VALIDATION_SECOND);
    }
    public String generateRefreshToken(String user) { return doGenerateToken(user, REFRESH_TOKEN_VALIDATION_SECOND); }

    public String doGenerateToken(String user, long expireTime) {

        Claims claims = Jwts.claims();
        claims.put("user", user);

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUser(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
