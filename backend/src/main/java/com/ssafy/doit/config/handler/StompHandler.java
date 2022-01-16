package com.ssafy.doit.config.handler;

import com.ssafy.doit.service.jwt.JwtUtil;
import com.ssafy.doit.service.jwt.RedisUtil;
import com.ssafy.doit.service.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final RedisUtil redisUtil;
    @Autowired
    private final UserDetailsServiceImpl userDetailsService;

    public Message<?> preSend(Message<?>  message, MessageChannel channel){
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            String refreshJwt = (String)sessionAttributes.get("refreshToken");

            String refreshUser = redisUtil.getData(refreshJwt);

            if(refreshUser.equals(jwtUtil.getUser(refreshJwt))){
                UserDetails userDetails = userDetailsService.loadUserByUsername(refreshUser);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            Principal user = SecurityContextHolder.getContext().getAuthentication();
            accessor.setUser(user);
        }

        return message;
    }
}
