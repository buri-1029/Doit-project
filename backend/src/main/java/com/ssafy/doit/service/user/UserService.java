package com.ssafy.doit.service.user;

import com.ssafy.doit.model.request.RequestChangePw;
import com.ssafy.doit.model.response.ResponseUser;
import com.ssafy.doit.model.user.User;
import com.ssafy.doit.repository.UserRepository;
import com.ssafy.doit.service.S3Service;
import com.ssafy.doit.service.jwt.CookieUtil;
import com.ssafy.doit.service.jwt.JwtUtil;
import com.ssafy.doit.service.jwt.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private final CookieUtil cookieUtil;
    @Autowired
    private final RedisUtil redisUtil;

    public Long currentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Long userPk = userRepository.findByEmail(userDetails.getUsername()).get().getId();

        return  userPk;
    }

    // 회원 상세 정보
    @Transactional
    public ResponseUser detailUser(Long userPk){
        User getUser = userRepository.findById(userPk).get();
        ResponseUser user = new ResponseUser(getUser);
        return user;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResponseUser currentUser = detailUser(currentUser());
        final String token = jwtUtil.generateToken(currentUser.getEmail());
        final String refresh = jwtUtil.generateRefreshToken(currentUser.getEmail());

        Cookie accessToken = cookieUtil.deleteCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
        Cookie refreshToken = cookieUtil.deleteCookie(JwtUtil.REFRESH_TOKEN_NAME, refresh);
        redisUtil.deleteData(refresh);

        response.addCookie(accessToken);
        response.addCookie(refreshToken);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null)
            new SecurityContextLogoutHandler().logout(request, response, auth);
    }

    // 회원정보 수정
    public void updateUser(Long userPk, User userReq, MultipartFile file){
//        Optional<User> user = userRepository.findById(userPk);
//        user.ifPresent(selectUser->{
//            String imgPath = null;
//                try {
//                    imgPath = s3Service.upload(userReq.getImage(),file);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                selectUser.setImage(imgPath);
//
//            selectUser.setNickname(userReq.getNickname());
//            userRepository.save(selectUser);
//        });
    }


}
