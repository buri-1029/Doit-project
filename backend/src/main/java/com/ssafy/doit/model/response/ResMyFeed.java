package com.ssafy.doit.model.response;

import com.ssafy.doit.model.feed.Feed;
import lombok.Getter;

@Getter
public class ResMyFeed {
    private Long feedPk;

    private String media;
    private String content;
    private Long userPk;
    private String writer;
    private String feedType;

    private String authCheck;

    private Long groupPk;
    private String groupName;
    private String createDate;
    private String updateDate;

    public ResMyFeed(Feed feed, String nickname, String groupName){
        this.feedPk = feed.getFeedPk();
        this.writer = nickname;
        this.userPk = feed.getWriter();
        this.content = feed.getContent();
        this.feedType = feed.getFeedType();
        this.authCheck = feed.getAuthCheck();
        this.groupPk = feed.getGroupPk();
        this.groupName = groupName;
        this.createDate = feed.getCreateDate().toString();
        this.updateDate = feed.getUpdateDate();
        this.media = feed.getMedia();
    }
}
