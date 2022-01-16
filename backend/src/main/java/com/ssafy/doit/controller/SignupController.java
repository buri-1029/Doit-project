package com.ssafy.doit.controller;

import com.ssafy.doit.model.Mileage;
import com.ssafy.doit.model.feed.CommitUser;
import com.ssafy.doit.model.request.RequestChangePw;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.model.user.User;
import com.ssafy.doit.model.user.UserRole;
import com.ssafy.doit.repository.MileageRepository;
import com.ssafy.doit.repository.UserRepository;
import com.ssafy.doit.repository.feed.CommitUserRepository;
import com.ssafy.doit.service.mail.EmailSendService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class SignupController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailSendService emailSendService;
    @Autowired
    private MileageRepository mileageRepository;
    @Autowired
    private CommitUserRepository commitUserRepository;

    private final PasswordEncoder passwordEncoder;

    // 닉네임 중복 확인
    @ApiOperation(value = "닉네임 중복 확인")
    @PostMapping("/checkNick")
    public Object checkNickname(@RequestBody String nickname){
        ResponseBasic result = new ResponseBasic();
        System.out.println(UserRole.USER);
        Optional<User> optUser = userRepository.findByNicknameAndUserRole(nickname, UserRole.USER);
        Optional<User> optGuest = userRepository.findByNicknameAndUserRole(nickname, UserRole.GUEST);
        if(optUser.isPresent() || optGuest.isPresent()){
            result = new ResponseBasic(false, "중복된 닉네임입니다.", null);
        }else{
            result = new ResponseBasic( true, "success", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 이메일 중복 확인
    @ApiOperation(value = "이메일 중복 확인")
    @PostMapping("/checkEmail")
    public Object checkEmail(@RequestBody String email) {
        ResponseBasic result = new ResponseBasic();
        if (userRepository.findByEmail(email).isPresent()) {
            result = new ResponseBasic(false, "중복된 이메일 입니다.", null);
        } else {
            result = new ResponseBasic( true, "success", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    // 로그인 전 비밀번호 찾기 이메일 확인
    @ApiOperation(value = "비번 찾기 이메일 확인")
    @PostMapping("/confirmEmail")
    public Object confirmEmail(@RequestBody String email) {
        ResponseBasic result = null;
        Optional<User> optWithdraw = userRepository.findByEmailAndUserRole(email, UserRole.WITHDRAW);
        Optional<User> user =  userRepository.findByEmail(email);

        if(user.isPresent()) {
            if(optWithdraw.isPresent()){
                result = new ResponseBasic(false, "해당 이메일이 존재하지 않습니다.", null);
            }else{
                result = new ResponseBasic( true, "success", null);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }else {
            result = new ResponseBasic(false, "해당 이메일이 존재하지 않습니다.", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 회원가입
    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public Object signup(@RequestBody User request) {
        ResponseBasic result = null;
        try {
            String authKey = emailSendService.sendSignupMail(request.getEmail());
            User user = userRepository.save(User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .createDate(LocalDate.now())
                    .userRole(UserRole.GUEST)
                    .authKey(authKey)
                    .build());

            beforeCommitUser(user.getId());
            mileageRepository.save(Mileage.builder()
                    .content("회원가입 축하 마일리지 지급")
                    .date(LocalDateTime.now())
                    .mileage("+1,000")
                    .user(user).build());

            result = new ResponseBasic( true, "success", null);
        }
        catch (Exception e){
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 회원가입 이메일 인증 전송
    @ApiOperation(value = "회원가입 이메일 인증 전송")
    @PostMapping("/sendSignupEmail")
    public void sendSignupEmail(@RequestBody User request){
        String authKey = emailSendService.sendSignupMail(request.getEmail());
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        user.ifPresent(selectUser ->{
            selectUser.setAuthKey(authKey);
            userRepository.save(selectUser);
        });
    }

    // 회원가입 이메일 인증 확인
    @ApiOperation(value = "회원가입 이메일 인증 확인")
    @GetMapping("/confirmSignup")
    public Object confirmSignup(@RequestParam String email, @RequestParam String authKey){
        ResponseBasic result = null;
        try{
            Optional<User> user = userRepository.findByEmailAndAuthKey(email, authKey);
            user.ifPresent(selectUser ->{
                selectUser.setUserRole(UserRole.USER);
                userRepository.save(selectUser);
            });
            result = new ResponseBasic( true, "success", null);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic( false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 비밀번호 변경(찾기) 이메일 인증 전송
    @ApiOperation(value = "비밀번호 변경(찾기) 이메일 인증 전송")
    @PostMapping("/sendChangePwEmail")
    public Object sendChangePwEmail(@RequestBody User request){
        ResponseBasic result = null;
        String authKey = emailSendService.sendChangePwMail(request.getEmail());
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if(!user.isPresent()) {
            result = new ResponseBasic( false, "해당 이메일이 존재하지 않습니다.", null);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        user.ifPresent(selectUser -> {
            selectUser.setAuthKey(authKey);
            userRepository.save(selectUser);
        });
        result = new ResponseBasic( true, "success", null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 비밀번호 변경(찾기) 이메일 인증 확인
    @ApiOperation(value = "비밀번호 변경(찾기) 이메일 인증 확인")
    @PostMapping("/confirmPw")
    public Object confirmPw(@RequestBody RequestChangePw request){
        ResponseBasic result = null;
        try{
            Optional<User> user = userRepository.findByEmailAndAuthKey(request.getEmail(), request.getAuthKey());
            user.ifPresent(selectUser ->{
                selectUser.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(selectUser);
            });
            result = new ResponseBasic( true, "success", null);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic( false, "이메일을 다시 한번 확인해주세요.", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public void beforeCommitUser(Long userPk){
        LocalDate now = LocalDate.now();
        LocalDate before = now.minusDays(29);
        for(int day = 1; day <= 30; day++){
            Optional<CommitUser> opt = commitUserRepository.findByUserPkAndDate(userPk,before);
            if(!opt.isPresent()) {
                commitUserRepository.save(CommitUser.builder()
                        .date(before)
                        .userPk(userPk)
                        .cnt(0)
                        .build());
                before = before.plusDays(1);
            }
        }
    }
}
