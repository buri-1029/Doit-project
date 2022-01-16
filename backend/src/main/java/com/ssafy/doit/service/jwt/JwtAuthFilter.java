package com.ssafy.doit.service.jwt;

import com.ssafy.doit.service.user.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final Cookie jwtToken = cookieUtil.getCookie(request, JwtUtil.ACCESS_TOKEN_NAME);

        String user = null;
        String jwt = null;
        String refreshJwt = null;
        String refreshUser = null;

        try{
            if(jwtToken != null){
                jwt = jwtToken.getValue();
                user = jwtUtil.getUser(jwt);
            }
            if(user != null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(user);
                if(jwtUtil.validateToken(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }catch (ExpiredJwtException e){
            Cookie refreshToken = cookieUtil.getCookie(request, JwtUtil.REFRESH_TOKEN_NAME);
            if(refreshToken != null) {
                refreshJwt = refreshToken.getValue();
            }
        } catch (Exception e){
            throw new RuntimeException("jwt 검증 오류");
        }

        try{
            if(refreshJwt != null){
                refreshUser = redisUtil.getData(refreshJwt);

                if(refreshUser.equals(jwtUtil.getUser(refreshJwt))){
                    UserDetails userDetails = userDetailsService.loadUserByUsername(refreshUser);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);


                    String newToken =jwtUtil.generateToken(refreshUser);

                    Cookie newAccessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME,newToken);
                    response.addCookie(newAccessToken);
                }
            }
        }catch(ExpiredJwtException e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        } catch (Exception e){
            throw new RuntimeException("jwt 검증 오류");
        }

        filterChain.doFilter(request, response);
    }
}
