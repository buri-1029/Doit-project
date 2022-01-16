package com.ssafy.doit.model.response;

import com.ssafy.doit.model.group.Group;
import lombok.Getter;

@Getter
public class ResGroupList {
    private Long groupPk;
    private String name;
    private int score;
    private String image;
    private Long leader;

    public ResGroupList(Group group){
        this.groupPk = group.getGroupPk();
        this.name = group.getName();
        this.score = group.getScore();
        this.image = group.getImage();
        this.leader =group.getLeader();
    }
}
