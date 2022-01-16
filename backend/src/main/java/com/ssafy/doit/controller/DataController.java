package com.ssafy.doit.controller;

import com.ssafy.doit.model.*;
import com.ssafy.doit.model.feed.CommitGroup;
import com.ssafy.doit.model.feed.CommitUser;
import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.group.GroupUser;
import com.ssafy.doit.model.group.HashTag;
import com.ssafy.doit.model.response.ResRanking;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.model.user.User;
import com.ssafy.doit.model.user.UserRole;
import com.ssafy.doit.repository.*;
import com.ssafy.doit.repository.feed.CommitGroupRepository;
import com.ssafy.doit.repository.feed.CommitUserRepository;
import com.ssafy.doit.repository.group.GroupRepository;
import com.ssafy.doit.repository.group.HashTagRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final GroupRepository groupRepository;
    @Autowired
    private HashTagRepository hashTagRepository;
    @Autowired
    private CommitUserRepository commitUserRepository;
    @Autowired
    private CommitGroupRepository commitGroupRepository;
    @Autowired
    private MileageRepository mileageRepository;

    // 해시태그 가장 많이 사용된 순서대로 데이터 제공
    @ApiOperation(value = "WordCloud 해시태그 데이터")
    @GetMapping("/wordCloud")
    public Object wordCloud(){
        ResponseBasic result = null;
        try {
            List<HashTag> list = hashTagRepository.findTop50ByOrderByCntDesc();
            Map<String, Integer> resWC = new HashMap<>();
            for (HashTag ht : list) {
                if(ht.getCnt() > 0) {
                    resWC.put(ht.getName(), ht.getCnt());
                }
            }
            result = new ResponseBasic(true,"success", resWC);
        }catch(Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false,"fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 00시 마다
    public void initCommitUser(){
        List<User> userList = userRepository.findByUserRole(UserRole.USER);
        for(User user : userList){
            Long userPk = user.getId();
            LocalDate date = LocalDate.now();
            Optional<CommitUser> opt = commitUserRepository.findByUserPkAndDate(userPk,date);
            if(!opt.isPresent()) {
                commitUserRepository.save(CommitUser.builder()
                        .date(date)
                        .userPk(userPk)
                        .cnt(0)
                        .build());
            }
        }
    }

    // 잔디에 보낼 커밋수
    @ApiOperation(value = "잔디에 보낼 커밋(인증수) 데이터")
    @GetMapping("/grass")
    public Object grass(Long userPk){
        ResponseBasic result = null;
        try {
            LocalDate today = LocalDate.now();
            LocalDate before = today.minusDays(29); //59
            List<CommitUser> list = commitUserRepository.findTop30ByUserPkAndDateBetweenOrderByDate(userPk,before,today);
            List<Integer> resGrass = new ArrayList<>();
            for(CommitUser cu : list){
                resGrass.add(cu.getCnt());
            }
            result = new ResponseBasic(true,"success", resGrass);
        }catch(Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false,"fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 일간 그룹 순위 => 메인 화면에 노출되는 api
    @ApiOperation(value = "일간 그룹 순위 => 메인 화면에 노출")
    @GetMapping("/rankingGroup")
    public Object rankingGroup(){
        ResponseBasic result = null;
        try {
            List<CommitGroup> cgList = commitGroupRepository.findTop5ByDateOrderByCalcDesc(LocalDate.now());
            List<ResRanking> resList = new ArrayList<>();
            int rank = 1;
            for(CommitGroup cg : cgList){
                Group group = groupRepository.findById(cg.getGroupPk()).get();
                resList.add(new ResRanking(rank++,cg.getGroupPk(),group));
            }
            result = new ResponseBasic(true,"success", resList);
        }catch(Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false,"fail", null);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 주간 그룹 순위 => 그룹점수와 개인 마일리지 점수를 제공하는 function
    // 매주 월요일마다 그 전주(7일)동안 각 그룹의 인증수 비율을 합한 수대로 순위를 매겨
    // 1~5위부터 그룹 점수 제공하며 각 그룹에 속해 있는 그룹원들에게 마일리지 점수 제공
    // ((group table에 있는 score : 그 동안(주간 그룹 순위) 받은 누적 그룹 점수로 명예의 전당으로 활용 가능))
    @Scheduled(cron = "0 0 0 ? * MON") // 월요일 자정마다
    @Transactional
    public void giveScore(){
        LocalDate start = LocalDate.now().plusDays(1).minusDays(7);
        LocalDate end = LocalDate.now().plusDays(1).minusDays(1);
        List<CommitGroup> cgList = commitGroupRepository.weekRanking(start, end);
        int score = 300;
        for(CommitGroup cg : cgList){
            Group group = groupRepository.findById(cg.getGroupPk()).get();
            List<GroupUser> userList = group.userList;
            for(GroupUser gu : userList){
                User user = userRepository.findById(gu.getUser().getId()).get();
                user.setMileage(user.getMileage() + score);
                userRepository.save(user);
                mileageRepository.save(Mileage.builder()
                        .content("그룹순위에 따른 마일리지 지급")
                        .date(LocalDateTime.now())
                        .mileage("+"+score)
                        .user(user).build());
                // 알림보내기
            }
            group.setScore(group.getScore() + score);
            groupRepository.save(group);
            score -= 50;
        }
    }
}
