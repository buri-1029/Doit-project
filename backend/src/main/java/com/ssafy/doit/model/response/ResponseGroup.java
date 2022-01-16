package com.ssafy.doit.model.response;

import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.group.GroupHashTag;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseGroup {
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
    
    public ResponseGroup(Group group){
        this.groupPk = group.getGroupPk();
        this.name = group.getName();
        this.content = group.getContent();
        this.category = group.getCategory();
        this.createDate = group.getCreateDate().toString();
        this.endDate = group.getEndDate();
        this.leader = group.getLeader();
        this.score = group.getScore();
        this.totalNum = group.getTotalNum();
        this.maxNum = group.getMaxNum();
        this.image = group.getImage();
        this.tags = this.getTags(group);
    }
    public List<String> getTags(Group group){
        List<String> tag = new ArrayList<>();
        for(GroupHashTag hashTag : group.tagList){
            tag.add(hashTag.getHashTag().getName());
        }
        return tag;
    }
}
