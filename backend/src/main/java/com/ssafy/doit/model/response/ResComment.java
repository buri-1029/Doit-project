package com.ssafy.doit.model.response;


import com.ssafy.doit.model.feed.Comment;

import lombok.Getter;


import java.time.LocalDateTime;

@Getter
public class ResComment {

    private Long commentPk;

    private String content;
    private String image;
    private String nickname;
    private Long feedPk;
    private Long userPk;

    private LocalDateTime createDate;
    private String updateDate;

    public ResComment(Comment comment, String nickname, String image,Long userPk) {
            this.commentPk = comment.getCommentPk();
            this.content = comment.getContent();
            this.feedPk = comment.getFeedPk();
            this.nickname = nickname;
            this.image = image;
            this.createDate = comment.getCreateDate();
            this.userPk=userPk;
            this.updateDate = comment.getUpdateDate();

    }
}
