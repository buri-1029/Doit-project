package com.ssafy.doit.model.response;

import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.group.GroupHashTag;
import com.ssafy.doit.model.group.GroupUser;
import com.ssafy.doit.repository.group.GroupUserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResGroupDetail {

    @Autowired
    private GroupUserRepository groupUserRepository;

    private Long groupPk;
    private String name;
    private String content;
    private String category;
    private Long leader;
    private String createDate;
    private String endDate;
    private String image;
    private int score;
    private int totalNum;
    private int maxNum;

    private List<String> tags;
    private List<ResponseUser> users;

    public ResGroupDetail(Group group, List<GroupUser> users, int total){
        this.groupPk = group.getGroupPk();
        this.name = group.getName();
        this.content = group.getContent();
        this.category = group.getCategory();
        this.createDate = group.getCreateDate().toString();
        this.endDate = group.getEndDate();
        this.leader = group.getLeader();
        this.score = group.getScore();
        this.totalNum = total;
        this.maxNum = group.getMaxNum();
        this.image = group.getImage();
        this.tags = this.getTags(group);
        this.users = this.getUsers(users);
    }

    public List<String> getTags(Group group){
        List<String> tag = new ArrayList<>();
        for(GroupHashTag hashTag : group.tagList){
            tag.add(hashTag.getHashTag().getName());
        }
        return tag;
    }

    public List<ResponseUser> getUsers(List<GroupUser> list){
        List<ResponseUser> user = new ArrayList<>();
        for(GroupUser gu : list){
            user.add(new ResponseUser(gu.getUser()));
        }
        return user;
    }
}
