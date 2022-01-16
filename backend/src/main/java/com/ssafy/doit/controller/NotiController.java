package com.ssafy.doit.controller;

import com.ssafy.doit.model.notification.Notification;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.repository.NotiRepository;
import com.ssafy.doit.service.NotiService;
import com.ssafy.doit.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/noti")
public class NotiController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final NotiService notiService;
    @Autowired
    private final NotiRepository notiRepository;

    @ApiOperation(value = "현재 로그인한 유저의 알림 리스트")
    @GetMapping("/getList")
    public Object getList(){
        ResponseBasic result = null;
        try{
            Long uid = userService.currentUser();
            List<Notification> notifications = notiService.getList(uid);
            result = new ResponseBasic(true, "success", notifications);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "알림 확인")
    @GetMapping("/confirm")
    public Object confirm(@RequestParam Long id){
        ResponseBasic result = null;
        try{
            Long currentUser = userService.currentUser();
            notiService.delete(id);
            List<Notification> notifications = notiService.getList(currentUser);
            result = new ResponseBasic(true, "success", notifications);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
