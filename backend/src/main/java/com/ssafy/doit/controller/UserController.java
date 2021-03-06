package com.ssafy.doit.controller;

import com.ssafy.doit.model.Mileage;
import com.ssafy.doit.model.request.RequestChangePw;
import com.ssafy.doit.model.response.ResGroupList;
import com.ssafy.doit.model.response.ResponseMileage;
import com.ssafy.doit.model.response.ResponseUser;
import com.ssafy.doit.model.user.UserRole;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.model.request.RequestLoginUser;
import com.ssafy.doit.model.user.User;
import com.ssafy.doit.repository.MileageRepository;
import com.ssafy.doit.repository.UserRepository;
import com.ssafy.doit.service.feed.FeedService;
import com.ssafy.doit.service.group.GroupUserService;
import com.ssafy.doit.service.S3Service;
import com.ssafy.doit.service.jwt.CookieUtil;
import com.ssafy.doit.service.jwt.RedisUtil;
import com.ssafy.doit.service.user.UserService;
import com.ssafy.doit.service.jwt.JwtUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private final CookieUtil cookieUtil;
    @Autowired
    private final RedisUtil redisUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private GroupUserService groupUserService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private MileageRepository mileageRepository;
    @Autowired
    private final FeedService feedService;

    private final PasswordEncoder passwordEncoder;


    // ?????????
    @ApiOperation(value = "?????????")
    @PostMapping("/login")
    public Object login(@RequestBody RequestLoginUser userReq, HttpServletResponse response) {
        ResponseBasic result = new ResponseBasic();

        Optional<User> userOpt = userRepository.findByEmail(userReq.getEmail());
        if(!userOpt.isPresent()){
            result = new ResponseBasic(false, "?????? ???????????? ???????????? ????????????.", null);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }else{
            User user = userOpt.get();
            if(user.getUserRole().equals(UserRole.GUEST)){
                result = new ResponseBasic(false, "???????????? ????????? ?????? ??? ????????? ???????????????.", null);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else if(user.getUserRole().equals(UserRole.WITHDRAW)) {
                result = new ResponseBasic(false, "????????? ???????????? ????????? ???????????????.", null);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else{
                if (!passwordEncoder.matches(userReq.getPassword(), user.getPassword())) {
                    result = new ResponseBasic(false, "????????? ?????????????????????.", null);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }else {
                    String content = "????????? ???????????? ??????";
                    String today = LocalDate.now().toString();
                    Optional<Mileage> opt = mileageRepository.findByContentAndDateAndUser(content, today, user);
                    if(!opt.isPresent()){
                        user.setMileage(user.getMileage() + 50);
                        userRepository.save(user);
                        mileageRepository.save(Mileage.builder()
                                .content("????????? ???????????? ??????")
                                .date(LocalDateTime.now())
                                .mileage("+50")
                                .user(user).build());
                    }

                    final String token = jwtUtil.generateToken(user.getEmail());
                    final String refresh = jwtUtil.generateRefreshToken(user.getEmail());

                    Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
                    Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refresh);
                    redisUtil.setDataExpire(refresh, user.getEmail(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

                    response.addCookie(accessToken);
                    response.addCookie(refreshToken);

                    result = new ResponseBasic(true, "success", user);
                }
            }

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    // ????????????
    @ApiOperation(value = "????????????")
    @GetMapping("/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response){
        ResponseBasic result = new ResponseBasic();
        try {
            userService.logout(request, response);
            result = new ResponseBasic(true, "success", null);
        }catch (Exception e){
            result = new ResponseBasic(false, "????????? ??????", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ???????????? ????????? ??????
    @ApiOperation(value = "???????????? ?????? ??????")
    @GetMapping("/detailUser")
    public Object detailUser(){
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            ResponseUser user = userService.detailUser(userPk);
            result = new ResponseBasic(true, "success", user);
        } catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ???????????? ??????
    @ApiOperation(value = "???????????? ??????")
    @PutMapping("/updateInfo")
    public Object updateInfo(@RequestBody User userReq) {
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            Optional<User> user = userRepository.findById(userPk);

            user.ifPresent(selectUser->{
                selectUser.setNickname(userReq.getNickname());
                userRepository.save(selectUser);
            });
            result = new ResponseBasic(true, "???????????? ?????? success", null);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "???????????? ?????? fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ????????? ?????? ??????
    @ApiOperation(value = "????????? ????????? ?????? ??????")
    @PostMapping("/profile/checkNick")
    public Object checkNickname(@RequestBody String nickname){
        ResponseBasic result = null;
        Long userPk = userService.currentUser();

        Optional<User> user = userRepository.findById(userPk); //???????????? ??????
        Optional<User> optGuest = userRepository.findByNicknameAndUserRole(nickname, UserRole.GUEST); //GUEST ????????? ??????
        Optional<User> optUser = userRepository.findByNickname(nickname); //????????? ???????????? ?????? ??????

        if(optUser.isPresent()) {
            if((userPk == optUser.get().getId()) && (user.get().getNickname().equals(nickname))){
                result = new ResponseBasic( true, "success", null);
            }else{
                result = new ResponseBasic(false, "????????? ??????????????????.", null);
            }
        }else if(optGuest.isPresent()){
            result = new ResponseBasic(false, "????????? ??????????????????.", null);
        }else{
            result = new ResponseBasic( true, "success", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ???????????? ????????? ??????
    @ApiOperation(value = "???????????? ????????? ??????")
    @PostMapping("/updateImg")
    public Object updateImg(@RequestParam MultipartFile file) {
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            User currentUser = userRepository.findById(userPk).get();
            String imgPath = s3Service.upload(currentUser.getImage(),file);

            currentUser.setImage(imgPath);
            userRepository.save(currentUser);
            result = new ResponseBasic(true, "????????? ?????? ?????? success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "????????? ?????? ?????? fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ????????????????????? ???????????? ??????
    @ApiOperation(value = "???????????? ????????? ???????????? ??????")
    @PostMapping("/changePw")
    public Object changePw(@RequestBody RequestChangePw requestPw){
        ResponseBasic result = new ResponseBasic();
        try {
            Long userPk = userService.currentUser();
            User currentUser = userRepository.findById(userPk).get();
            currentUser.setPassword(passwordEncoder.encode(requestPw.getPassword()));
            userRepository.save(currentUser);
            result = new ResponseBasic(true, "success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "???????????? ?????? ??????", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ?????? ??????
    @ApiOperation(value = "?????? ??????")
    @PutMapping("/deleteUser")
    public Object deleteUser(HttpServletRequest request, HttpServletResponse response) {
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            List<ResGroupList> list = groupUserService.deleteGroupByUser(userPk);
            feedService.deleteFeedByUser(userPk);

            userService.logout(request, response);
            result = new ResponseBasic(true, "success", list);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ?????? ????????? ?????? (feed_open) & ?????? ????????? ?????? (group_open)
    // ????????? - ????????????
    @ApiOperation(value = "?????? ??????,?????? ????????? ??????/?????????")
    @PutMapping("/setOnAndOff")
    public Object setOnAndOff(@RequestParam String open ,@RequestParam String opt) {
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            User user = userRepository.findById(userPk).get();
            if(opt.equals("feed")) user.setFeedOpen(open);
            else if(opt.equals("group")) user.setGroupOpen(open);
            userRepository.save(user);
            result = new ResponseBasic( true, "success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic( false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ???????????? ?????? ?????????
    @ApiOperation(value = "???????????? ?????? ?????????")
    @GetMapping("/mileageList")
    public Object mileageList(@RequestParam Long userPk){
        ResponseBasic result = null;
        try {
            Long loginPk = userService.currentUser();
            if(loginPk.equals(userPk)){
                User user = userRepository.findById(loginPk).get();
                List<Mileage> mileageList = mileageRepository.findAllByUser(user);
                List<ResponseMileage> resList = new ArrayList<>();
                for(Mileage mileage : mileageList){
                    resList.add(new ResponseMileage(mileage));
                }
                result = new ResponseBasic( true, "success", resList);
            }
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
