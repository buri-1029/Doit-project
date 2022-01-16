package com.ssafy.doit.service.group;

import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.group.GroupHashTag;
import com.ssafy.doit.model.group.GroupUser;
import com.ssafy.doit.model.group.HashTag;
import com.ssafy.doit.model.request.RequestGroup;
import com.ssafy.doit.model.request.RequestPage;
import com.ssafy.doit.model.response.ResGroupDetail;
import com.ssafy.doit.model.response.ResponseGroup;

import com.ssafy.doit.repository.group.GroupHashTagRepository;
import com.ssafy.doit.repository.group.GroupRepository;
import com.ssafy.doit.repository.group.GroupUserRepository;
import com.ssafy.doit.repository.group.HashTagRepository;
import com.ssafy.doit.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupHashTagService {

    @Autowired
    private S3Service s3Service;
    @Autowired
    private final GroupRepository groupRepository;
    @Autowired
    private final HashTagRepository hashTagRepository;
    @Autowired
    private final GroupHashTagRepository groupHashTagRepository;
    @Autowired
    private GroupUserRepository groupUserRepository;

    // 특정 해시태그 포함한 그룹 찾기
    @Transactional
    public Page<ResponseGroup> findAllByHashTag(String hashtag, RequestPage pageable){
        Page<Group> groupList = null;
        if(hashtag.equals("")) {
            groupList = groupRepository.findAllByStatus("true", pageable.of());
        }else{
            groupList = groupRepository.findAllByHashTagAndStatus(hashtag, "true", pageable.of());
        }
        Page<ResponseGroup> resList = groupList.map(
                group -> new ResponseGroup(group));
        return resList;
    }

    // 카테고리에 따른 그룹 분류 리스트
    @Transactional
    public Page<ResponseGroup> findAllByCategory(String category, RequestPage pageable){
        Page<Group> groupList = groupRepository.findAllByCategoryAndStatus(category,"true",pageable.of());
        Page<ResponseGroup> resList = groupList.map(
                group -> new ResponseGroup(group));
        return resList;
    }

    // 선택한 그룹 정보 제공
    @Transactional
    public ResGroupDetail findByGroupPk(Long groupPk) {
        Group group = groupRepository.findById(groupPk).get();
        int total = groupUserRepository.countByGroupAndStatus(group, "true");
        List<GroupUser> users = groupUserRepository.findByGroupAndStatus(group, "true");
        return (new ResGroupDetail(group, users, total));
    }

    // 그룹 생성
    @Transactional
    public Long create(Long userPk, RequestGroup groupReq){
        // 그룹에 대한 정보 저장
        Group group = groupRepository.save(Group.builder()
                .name(groupReq.getName())
                .content(groupReq.getContent())
                .category(groupReq.getCategory())
                .maxNum(groupReq.getMaxNum())
                .createDate(LocalDate.now())
                .endDate(groupReq.getEndDate())
                .leader(userPk)
                .build());

        List<String> hashtags = groupReq.getHashtags();
        Long groupPk = group.getGroupPk();
        for(String hashtag : hashtags){
            findOrCreateHashTag(group, hashtag);
        }
        return groupPk;
    }

    // 그룹 정보 수정
    @Transactional
    public void updateGroup(Long userPk, Group groupReq) throws Exception {
        Optional<Group> group = groupRepository.findById(groupReq.getGroupPk());
        if(userPk.equals(group.get().getLeader())){
            System.out.println(userPk +" "+ group.get().getLeader());
            group.ifPresent(selectGroup ->{
                selectGroup.setName(groupReq.getName());
                selectGroup.setContent(groupReq.getContent());
                selectGroup.setCategory(groupReq.getCategory());
                selectGroup.setMaxNum(groupReq.getMaxNum());
                selectGroup.setEndDate(groupReq.getEndDate());
                selectGroup.setLeader(groupReq.getLeader());
                groupRepository.save(selectGroup);
            });
        }else throw new Exception("그룹장이 아닙니다."); // 로그인한 유저가 그룹장이 아니면 수정불가
    }

    // 그룹 이미지 등록/수정
    @Transactional
    public void updateImg(Long groupPk, MultipartFile file) throws Exception {
        Group group = groupRepository.findById(groupPk).get();
        String imgPath = s3Service.upload(group.getImage(),file);
        group.setImage(imgPath);
        groupRepository.save(group);
    }

    // 그룹 해시태그 추가
    @Transactional
    public void updateHashTag(Long userPk, Long groupPk, String tag) throws Exception {
        Optional<Group> group = groupRepository.findById(groupPk);
        if(userPk.equals(group.get().getLeader())){
            Optional<HashTag> hashTag =  hashTagRepository.findByName(tag);
            if(hashTag.isPresent()){
                Optional<GroupHashTag> gh = groupHashTagRepository.findByGroupAndHashTag(group.get(),hashTag.get());
                if(gh.isPresent()) throw new Exception("해시태그가 이미 존재합니다."); // 이미 해시태그 존재
            }
            findOrCreateHashTag(group.get(), tag);
        } else throw new Exception("그룹장이 아닙니다."); // 로그인한 유저가 그룹장이 아니면 수정불가
    }

    // 그룹 해시태그 삭제
    @Transactional
    public void deleteHashTag(Long userPk, Long groupPk, String hashtag) throws Exception {
        Optional<Group> group = groupRepository.findById(groupPk);
        if(userPk.equals(group.get().getLeader())){
            HashTag hashTag =  hashTagRepository.findByName(hashtag).get();
            GroupHashTag gh = groupHashTagRepository.findByGroupAndHashTag(group.get(),hashTag).get();
            groupHashTagRepository.delete(gh);
            hashTag.setCnt(hashTag.getCnt() - 1);
            hashTagRepository.save(hashTag);
        }else throw new Exception("그룹장이 아닙니다."); // 로그인한 유저가 그룹장이 아니면 수정불가
    }

    // 해시태그 있으면 추가, 없으면 cnt +1
    @Transactional
    public void findOrCreateHashTag(Group group, String hashtag){
        // 입력한 해시태그들이
        Optional<HashTag> opt = hashTagRepository.findByName(hashtag);
        HashTag tag;
        if(opt.isPresent()){        // 기존에 있으면 cnt +1
            tag = opt.get();
            tag.setCnt(tag.getCnt() + 1);
        }else {                     // 기존에 없으면 새로 생성
            tag = hashTagRepository.save(HashTag.builder()
                    .name(hashtag).cnt(1).build());
        }
        // 그룹과 해시태그 연결 테이블에도 PK 값으로 저장
        groupHashTagRepository.save(GroupHashTag.builder()
                .group(group).hashTag(tag).build());
    }
}