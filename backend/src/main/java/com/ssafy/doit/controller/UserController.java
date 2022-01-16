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


    // 로그인
    @ApiOperation(value = "로그인")
    @PostMapping("/login")
    public Object login(@RequestBody RequestLoginUser userReq, HttpServletResponse response) {
        ResponseBasic result = new ResponseBasic();

        Optional<User> userOpt = userRepository.findByEmail(userReq.getEmail());
        if(!userOpt.isPresent()){
            result = new ResponseBasic(false, "해당 이메일이 존재하지 않습니다.", null);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }else{
            User user = userOpt.get();
            if(user.getUserRole().equals(UserRole.GUEST)){
                result = new ResponseBasic(false, "회원가입 이메일 인증 후 로그인 가능합니다.", null);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else if(user.getUserRole().equals(UserRole.WITHDRAW)) {
                result = new ResponseBasic(false, "탈퇴한 회원으로 로그인 불가합니다.", null);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else{
                if (!passwordEncoder.matches(userReq.getPassword(), user.getPassword())) {
                    result = new ResponseBasic(false, "잘못된 비밀번호입니다.", null);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }else {
                    String content = "로그인 마일리지 지급";
                    String today = LocalDate.now().toString();
                    Optional<Mileage> opt = mileageRepository.findByContentAndDateAndUser(content, today, user);
                    if(!opt.isPresent()){
                        user.setMileage(user.getMileage() + 50);
                        userRepository.save(user);
                        mileageRepository.save(Mileage.builder()
                                .content("로그인 마일리지 지급")
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

    // 로그아웃
    @ApiOperation(value = "로그아웃")
    @GetMapping("/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response){
        ResponseBasic result = new ResponseBasic();
        try {
            userService.logout(request, response);
            result = new ResponseBasic(true, "success", null);
        }catch (Exception e){
            result = new ResponseBasic(false, "로그인 실패", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 로그인한 사용자 정보
    @ApiOperation(value = "로그인한 회원 정보")
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

    // 회원정보 수정
    @ApiOperation(value = "회원정보 수정")
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
            result = new ResponseBasic(true, "회원정보 수정 success", null);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "회원정보 수정 fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 닉네임 중복 확인
    @ApiOperation(value = "프로필 닉네임 중복 확인")
    @PostMapping("/profile/checkNick")
    public Object checkNickname(@RequestBody String nickname){
        ResponseBasic result = null;
        Long userPk = userService.currentUser();

        Optional<User> user = userRepository.findById(userPk); //로그인한 회원
        Optional<User> optGuest = userRepository.findByNicknameAndUserRole(nickname, UserRole.GUEST); //GUEST 권한의 회원
        Optional<User> optUser = userRepository.findByNickname(nickname); //입력한 닉네임을 가진 회원

        if(optUser.isPresent()) {
            if((userPk == optUser.get().getId()) && (user.get().getNickname().equals(nickname))){
                result = new ResponseBasic( true, "success", null);
            }else{
                result = new ResponseBasic(false, "중복된 닉네임입니다.", null);
            }
        }else if(optGuest.isPresent()){
            result = new ResponseBasic(false, "중복된 닉네임입니다.", null);
        }else{
            result = new ResponseBasic( true, "success", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 회원정보 이미지 수정
    @ApiOperation(value = "회원정보 이미지 수정")
    @PostMapping("/updateImg")
    public Object updateImg(@RequestParam MultipartFile file) {
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            User currentUser = userRepository.findById(userPk).get();
            String imgPath = s3Service.upload(currentUser.getImage(),file);

            currentUser.setImage(imgPath);
            userRepository.save(currentUser);
            result = new ResponseBasic(true, "프로필 사진 변경 success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "프로필 사진 변경 fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 마이페이지에서 비밀번호 변경
    @ApiOperation(value = "로그인한 사용자 비밀번호 변경")
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
            result = new ResponseBasic(false, "비밀번호 변경 실패", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 회원 탈퇴
    @ApiOperation(value = "회원 탈퇴")
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

    // 피드 리스트 공개 (feed_open) & 그룹 리스트 공개 (group_open)
    // 비공개 - 나만보기
    @ApiOperation(value = "회원 피드,그룹 리스트 공개/비공개")
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

    // 마일리지 내역 리스트
    @ApiOperation(value = "마일리지 내역 리스트")
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
