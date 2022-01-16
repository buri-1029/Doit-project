package com.ssafy.doit.controller;

import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.response.ResGroupList;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.model.store.Product;
import com.ssafy.doit.model.user.User;
import com.ssafy.doit.model.user.UserRole;
import com.ssafy.doit.repository.*;
import com.ssafy.doit.repository.group.GroupRepository;
import com.ssafy.doit.repository.store.ProductRepository;
import com.ssafy.doit.service.AdminService;
import com.ssafy.doit.service.feed.FeedService;
import com.ssafy.doit.service.S3Service;
import com.ssafy.doit.service.group.GroupUserService;
import com.ssafy.doit.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupUserService groupUserService;
    @Autowired
    private final FeedService feedService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private ProductRepository productRepository;
    // 관리자 - 회원 리스트
    @ApiOperation(value = "관리자 - 회원 리스트")
    @GetMapping("/searchAllUser")
    public Object searchAllUser(){ // 페이징 처리하기
        ResponseBasic result = null;
        try {
        List<User> list = userRepository.findAll();
            result = new ResponseBasic(true, "success", list);
        }catch (Exception e){
        e.printStackTrace();
        result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 관리자 - 회원 강퇴
    @ApiOperation(value = "관리자 - 회원 강퇴")
    @PutMapping("/deleteUserByAdmin")
    public Object deleteUserByAdmin(@RequestParam Long userPk) {
        ResponseBasic result = null;
        try {
            Long adminPk = userService.currentUser();
            User Admin = userRepository.findById(adminPk).get();
            if(Admin.getUserRole().equals(UserRole.ADMIN)) {
                Optional<User> userInfo = userRepository.findById(userPk);
                if (userInfo.isPresent()) {
                    List<ResGroupList> list  = groupUserService.deleteGroupByUser(userPk);
                    feedService.deleteFeedByUser(userPk);
                    result = new ResponseBasic(true, "success", list);
                }
            }else throw new Exception("관리자가 아닙니다.");
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 관리자 - 그룹 리스트
    @ApiOperation(value = "관리자 - 그룹 리스트")
    @GetMapping("/searchAllGroup")
    public Object searchAllGroup(){ // 페이징 처리하기
        ResponseBasic result = null;
        try {
            List<Group> list = groupRepository.findAll();
            result = new ResponseBasic(true, "success", list);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 관리자 - 그룹 삭제
    @ApiOperation(value = "관리자 - 그룹 삭제")
    @PutMapping("/deleteGroupByAdmin")
    public Object deleteGroupByAdmin(@RequestParam Long groupPk) {
        ResponseBasic result = null;
        try {
            Long loginPk = userService.currentUser();
            User user = userRepository.findById(loginPk).get();
            if(user.getUserRole().equals(UserRole.ADMIN)) {
                adminService.deleteGroupByAdmin(groupPk);
                feedService.deleteFeedByGroup(groupPk);
            }else throw new Exception("관리자가 아닙니다.");
            result = new ResponseBasic(true, "success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 관리자 - 피드 삭제
    @ApiOperation(value = "관리자 - 피드 삭제")
    @PutMapping("/deleteFeedByAdmin")
    public Object deleteFeedByAdmin(@RequestParam Long feedPk) {
        ResponseBasic result = null;
        try {
            Long loginPk = userService.currentUser();
            User user = userRepository.findById(loginPk).get();
            if(user.getUserRole().equals(UserRole.ADMIN)) {
                adminService.deleteFeed(feedPk);
            }else throw new Exception("관리자가 아닙니다.");
            result = new ResponseBasic(true, "success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    // 관리자 - 상점 물품 삭제
    @ApiOperation(value = "관리자 - 상점 물품 삭제")
    @DeleteMapping("/deleteProductByAdmin/{id}")
    public Object deleteProductByAdmin(@PathVariable Long id) {
        ResponseBasic result = null;
        try {
            Long loginPk = userService.currentUser();
            User user = userRepository.findById(loginPk).get();
            Product origin = productRepository.findById(id).get();
            if(user.getUserRole().equals(UserRole.ADMIN)) {
                s3Service.deleteFile(origin.getImage());
                productRepository.delete(origin);
            }else throw new Exception("관리자가 아닙니다.");
            result = new ResponseBasic(true, "success", null);
        }
        catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, "fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
