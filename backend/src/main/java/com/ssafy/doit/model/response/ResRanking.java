package com.ssafy.doit.model.response;

import com.ssafy.doit.model.group.Group;
import lombok.Getter;

@Getter
public class ResRanking {
    private int ranking;
    private Long groupPk;
    private String groupName;
    private String image;

    public ResRanking(int ranking, Long groupPk, Group group){
        this.ranking = ranking;
        this.groupPk = groupPk;
        this.groupName = group.getName();
        this.image = group.getImage();
    }
}
