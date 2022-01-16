package com.ssafy.doit.service.group;

import com.ssafy.doit.model.*;
import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.group.GroupUser;
import com.ssafy.doit.model.response.ResGroupList;
import com.ssafy.doit.model.user.User;
import com.ssafy.doit.model.user.UserRole;
import com.ssafy.doit.repository.*;
import com.ssafy.doit.repository.feed.CommitUserRepository;
import com.ssafy.doit.repository.group.GroupRepository;
import com.ssafy.doit.repository.group.GroupUserRepository;
import com.ssafy.doit.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupUserService {

    @Autowired
    private GroupUserRepository groupUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MileageRepository mileageRepository;
    @Autowired
    private CommitUserRepository commitUserRepository;
    @Autowired
    private S3Service s3Service;

    // 가입 그룹 리스트
    @Transactional
    public List<ResGroupList> findGroupByUserPk(Long userPk){
        User user = userRepository.findById(userPk).get();
        List<GroupUser> groupList = groupUserRepository.findByUserAndStatus(user,"true");
        List<ResGroupList> list = new ArrayList<>();
        for(GroupUser gu : groupList){
            list.add(new ResGroupList(gu.getGroup()));
        }
        return list;
    }

    // 그룹 가입하기
    @Transactional
    public void join(Long userPk, Long groupPk) throws Exception {
        User user = userRepository.findById(userPk).get();
        Optional<Group> optGroup = groupRepository.findByGroupPkAndStatus(groupPk, "true");
        if(!optGroup.isPresent()) throw new Exception("활동하지 않는 그룹입니다.");

        Group group = optGroup.get();
        Optional<GroupUser> opt = groupUserRepository.findByGroupAndUser(group, user);

        if(!opt.isPresent()){
            if(group.getTotalNum() == group.getMaxNum()) throw new Exception("인원이 가득 찼습니다.");
            groupUserRepository.save(GroupUser.builder()
                    .group(group).user(user).status("true").build());

            group.setTotalNum(group.getTotalNum() + 1);
            groupRepository.save(group);

            user.setMileage(user.getMileage() + 100);
            userRepository.save(user);

            mileageRepository.save(Mileage.builder()
                    .content("그룹가입 축하 마일리지 지급")
                    .date(LocalDateTime.now())
                    .mileage("+100")
                    .user(user).build());
        }else{
            if(opt.get().getStatus().equals("false"))
                throw new Exception("탈퇴하였거나 강퇴당한 그룹입니다.");
            else
                throw new Exception("이미 가입된 그륩입니다.");
        }
    }

    // 그룹 탈퇴하기
    @Transactional
    public void withdrawGroupUser(Long userPk, Long groupPk) throws Exception {
        User user = userRepository.findById(userPk).get();
        Group group = groupRepository.findById(groupPk).get();
        if(userPk.equals(group.getLeader())) throw new Exception("그룹장은 탈퇴할 수 없습니다.");

        Optional<GroupUser> optGU = groupUserRepository.findByGroupAndUser(group, user);
        if(optGU.isPresent()) {
            optGU.ifPresent(selectGU -> selectGU.setStatus("false"));

            group.setTotalNum(group.getTotalNum() - 1); //회원 수 감소
            groupRepository.save(group);

            user.setMileage(user.getMileage() - 1500);
            userRepository.save(user);

            mileageRepository.save(Mileage.builder()
                    .content("그룹탈퇴 마일리지 차감")
                    .date(LocalDateTime.now())
                    .mileage("-1,500")
                    .user(user).build());
        }else throw new Exception("가입되어 있지 않은 그룹원입니다.");
    }

    // 그룹 내 그룹원 강퇴시키기 (그룹장이)
    @Transactional
    public void kickOutGroupUser(Long userPk, Long groupPk, Long leader) throws Exception {
        Group group = groupRepository.findById(groupPk).get();
        if(leader.equals(group.getLeader())){
            User user = userRepository.findById(userPk).get(); // 강퇴시킬 그룹원
            Optional<GroupUser> optGU = groupUserRepository.findByGroupAndUser(group, user);
            optGU.ifPresent(selectGU -> selectGU.setStatus("false"));

            group.setTotalNum(group.getTotalNum() - 1); // 회원 수 감소
            groupRepository.save(group);

            user.setMileage(user.getMileage() - 1000);
            userRepository.save(user);

            mileageRepository.save(Mileage.builder()
                    .content("그룹강퇴 마일리지 차감")
                    .date(LocalDateTime.now())
                    .mileage("-1,000")
                    .user(user).build());
        }else throw new Exception("그룹장이 아닙니다.");
    }

    // 회원 탈퇴(강퇴), 가입된 모든 그룹에서 delete
    @Transactional
    public List<ResGroupList> deleteGroupByUser(Long userPk) throws Exception {
        List<ResGroupList> resList = new ArrayList<>();
        User user = userRepository.findById(userPk).get();
        List<GroupUser> list = groupUserRepository.findByUser(user);

        for (GroupUser groupUser : list) {
            groupUserRepository.delete(groupUser);
            Group group = groupRepository.findById(groupUser.getGroup().getGroupPk()).get();
            if(group.getTotalNum() == 1){
                group.setStatus("false");
                groupRepository.save(group); continue;
            }
            Long newLeader = groupUserRepository.findTopByGroup(group).getUser().getId();
            group.setTotalNum(group.getTotalNum() - 1);     // 회원 수 감소
            if(userPk == group.getLeader()) {
                group.setLeader(newLeader);     // 임의의 그룹원으로 그룹장 지정
                resList.add(new ResGroupList(group));
            }
            groupRepository.save(group);
        }
        commitUserRepository.deleteByUserPk(userPk);
        //탈퇴 시 이미지 삭제 - 서버 & 디비
        s3Service.deleteFile(user.getImage());
        user.setImage(null);
        user.setUserRole(UserRole.WITHDRAW);
        userRepository.save(user);
        return resList;
    }
}