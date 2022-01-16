package com.ssafy.doit.controller;

import com.ssafy.doit.model.response.ResMyFeed;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.model.feed.Feed;
import com.ssafy.doit.model.response.ResponseFeed;
import com.ssafy.doit.model.response.ResponseUser;
import com.ssafy.doit.service.feed.FeedService;
import com.ssafy.doit.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final FeedService feedService;

    // 오늘 하루 인증 피드 등록한 그룹원 리스트
    @ApiOperation(value = "오늘 하루 인증 피드 등록한 그룹원 리스트")
    @GetMapping("/todayAuthUser")
    public Object todayAuthUser(@RequestParam Long groupPk){
        ResponseBasic result = null;
        try {
            //Long userPk = userService.currentUser();
            List<ResponseUser> list = feedService.todayAuthUser(groupPk);
            result = new ResponseBasic(true, "success", list);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 내 피드 생성
    @ApiOperation(value = "그룹 내 피드 생성")
    @PostMapping("/createFeed")
    public Object createFeed(@RequestBody Feed feedReq){
        ResponseBasic result = null;
        try{
            Long userPk = userService.currentUser();
            Long feedPk = feedService.createFeed(userPk,feedReq);
            result = new ResponseBasic(true, "success", feedPk);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 피드 이미지 등록/수정
    @ApiOperation(value = "피드 이미지 등록/수정")
    @PostMapping("/updateImg")
    public Object updateImg(@RequestParam Long feedPk, @RequestParam MultipartFile file) {
        ResponseBasic result = null;
        try {
            feedService.updateImg(feedPk, file);
            result = new ResponseBasic(true, "success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 내 피드 리스트
    @ApiOperation(value = "그룹 내 피드 리스트")
    @GetMapping("/groupFeed")
    public Object groupFeedList(@RequestParam Long groupPk, @RequestParam String start, @RequestParam String end){
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            List<ResponseFeed> list = feedService.groupFeedList(userPk, groupPk, start, end);
            result = new ResponseBasic(true, "success", list);
        }catch (Exception e) {
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 개인 피드 리스트 (+ 다른유저 피드리스트)
    @ApiOperation(value = "개인 피드 리스트 (+ 다른유저 피드리스트)")
    @GetMapping("/userFeed")
    public Object userFeed(@RequestParam Long userPk){
        ResponseBasic result = null;
        try {
            List<ResMyFeed> list = feedService.userFeedList(userPk);
            result = new ResponseBasic(true, "success", list);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 개인 피드 수정
    @ApiOperation(value = "개인 피드 수정")
    @PutMapping("/updateFeed")
    public Object updateFeed(@RequestBody Feed feedReq){
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            feedService.updateFeed(userPk, feedReq);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 개인 피드 삭제
    @ApiOperation(value = "개인 피드 삭제")
    @DeleteMapping("/deleteFeed")
    public Object deleteFeed(Long feedPk){
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            feedService.deleteFeed(userPk, feedPk);
            result = new ResponseBasic(true, "success", null);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 인증피드 인증확인
    @ApiOperation(value = "인증피드 인증확인")
    @GetMapping("/authCheckFeed")
    public Object authCheckFeed(Long feedPk){
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            Feed feed = feedService.authCheckFeed(userPk,feedPk);
            result = new ResponseBasic(true, "success", feed);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}