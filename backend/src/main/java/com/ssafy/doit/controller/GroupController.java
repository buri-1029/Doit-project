package com.ssafy.doit.controller;

import com.ssafy.doit.model.request.RequestGroup;
import com.ssafy.doit.model.request.RequestPage;
import com.ssafy.doit.model.response.ResGroupList;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.response.ResGroupDetail;
import com.ssafy.doit.model.response.ResponseGroup;
import com.ssafy.doit.service.feed.FeedService;
import com.ssafy.doit.service.group.GroupHashTagService;
import com.ssafy.doit.service.group.GroupUserService;
import com.ssafy.doit.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/***
 * @author : 김부희
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private final UserService userService;
    @Autowired
    private GroupHashTagService groupHashTagService;
    @Autowired
    private GroupUserService groupUserService;
    @Autowired
    private final FeedService feedService;

    //해시태그에 따른 그룹 리스트
    @ApiOperation(value = "해시태그에 따른 그룹 리스트")
    @GetMapping("/searchGroup")
    public Object searchGroup(@RequestParam String tag, final RequestPage pageable) {
        ResponseBasic result = null;
        try {
            Page<ResponseGroup> list = groupHashTagService.findAllByHashTag(tag, pageable);
            result = new ResponseBasic(true, "success", list);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 카테고리에 따른 그룹 리스트
    @ApiOperation(value = "카테고리에 따른 그룹 리스트")
    @GetMapping("/categoryGroup")
    public Object categoryGroup(@RequestParam String category, final RequestPage pageable) {
        ResponseBasic result = null;
        try {
            Page<ResponseGroup> list = groupHashTagService.findAllByCategory(category, pageable);
            result = new ResponseBasic(true, "success", list);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 선택한 그룹 정보 제공
    @ApiOperation(value = "선택한 그룹 정보 제공")
    @GetMapping("/detailGroup")
    public Object detailGroup(@RequestParam Long groupPk){
        ResponseBasic result = null;
        try {
            ResGroupDetail group = groupHashTagService.findByGroupPk(groupPk);
            if(group == null) throw new Exception("그룹을 찾을 수 없습니다.");
            result = new ResponseBasic(true,"success",group);
        }catch (Exception e) {
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    // 그룹 생성
    @ApiOperation(value = "그룹 생성")
    @PostMapping("/createGroup")
    public Object createGroup(@RequestBody RequestGroup groupReq) {
        ResponseBasic result = null;
        try{
            Long userPk = userService.currentUser();
            Long groupPk = groupHashTagService.create( userPk, groupReq);
            groupUserService.join(userPk, groupPk);
            result = new ResponseBasic(true,"success", groupPk);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 정보 수정
    @ApiOperation(value = "그룹 수정")
    @PutMapping("/updateGroup")
    public Object updateGroup(@RequestBody Group groupReq) {
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            groupHashTagService.updateGroup(userPk, groupReq);
            result = new ResponseBasic(true, "success", null);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 이미지 등록/수정
    @ApiOperation(value = "그룹 이미지 등록/수정")
    @PostMapping("/updateImg")
    public Object updateImg(@RequestParam Long groupPk, @RequestParam MultipartFile file) {
        ResponseBasic result = null;
        try {
            groupHashTagService.updateImg(groupPk, file);
            result = new ResponseBasic(true, "success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 해시태그 추가
    @ApiOperation(value = "그룹 해시태그 추가")
    @PutMapping("/updateHashTag")
    public Object updateHashTag(Long groupPk, @RequestParam("hashtag") String hashtag) {
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            groupHashTagService.updateHashTag(userPk, groupPk, hashtag);
            result = new ResponseBasic(true, "success", null);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 해시태그 삭제
    @ApiOperation(value = "그룹 해시태그 삭제")
    @DeleteMapping("/deleteHashTag")
    public Object deleteHashTag(Long groupPk, @RequestParam("hashtag") String hashtag) {
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            groupHashTagService.deleteHashTag(userPk, groupPk, hashtag);
            result = new ResponseBasic(true, "success", null);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 가입 그룹 리스트
    @ApiOperation(value = "가입 그룹 리스트")
    @GetMapping("/joinedGroup")
    public Object joinedGroup(@RequestParam Long userPk){
        ResponseBasic result = null;
        try {
            List<ResGroupList> list = groupUserService.findGroupByUserPk(userPk);
            result = new ResponseBasic(true,"success",list);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 현재 로그인한 유저의 가입 그룹 리스트
    @ApiOperation(value = "현재 로그인한 유저의 가입 그룹 리스트")
    @GetMapping("/currentUserGroup")
    public Object currentUserGroup(){
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            List<ResGroupList> list = groupUserService.findGroupByUserPk(userPk);
            result = new ResponseBasic(true,"success",list);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 가입하기
    @ApiOperation(value = "그룹 가입하기")
    @GetMapping("/joinGroup")
    public Object joinGroup(@RequestParam Long groupPk){
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            groupUserService.join(userPk, groupPk);
            result = new ResponseBasic(true, "가입 완료되었습니다.", null);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 탈퇴하기
    @ApiOperation(value = "그룹 탈퇴하기")
    @DeleteMapping("/withdrawGroupUser")
    public Object withdrawGroupUser(@RequestParam Long groupPk){
        ResponseBasic result = null;
        try {
            Long userPk = userService.currentUser();
            groupUserService.withdrawGroupUser(userPk, groupPk);
            feedService.deleteFeedByGroupUser(userPk, groupPk); // 그룹 탈퇴한 유저의 피드 삭제
            result = new ResponseBasic(true,"success",null);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 그룹 내 그룹원 강퇴시키기
    @ApiOperation(value = "그룹 내 그룹원 강퇴시키기")
    @DeleteMapping("/kickOutGroupUser")
    public Object kickOutGroupUser(@RequestParam Long groupPk, @RequestParam Long userPk){
        ResponseBasic result = null;
        try {
            Long leader = userService.currentUser(); //로그인된 회원 - 그룹 리더
            groupUserService.kickOutGroupUser(userPk, groupPk, leader);
            feedService.deleteFeedByGroupUser(userPk, groupPk); // 그룹 탈퇴한 유저의 피드 삭제
            result = new ResponseBasic(true,"success",null);
        }catch (Exception e) {
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}