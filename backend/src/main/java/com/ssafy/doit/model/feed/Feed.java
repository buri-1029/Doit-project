package com.ssafy.doit.model.feed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "`feed`")
public class Feed{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedPk;

    private String media;
    private String content;
    private String feedType; // 인증피드 : true, 자유피드 : false

    private int authCnt;
    private String authCheck;

    private LocalDateTime createDate;
    private String updateDate;

    private String status;
    private Long groupPk;
    @Column(name = "`user_pk`")
    private Long writer; // writer

    @JsonIgnore
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "feed")
    public List<FeedUser> userList; // 인증확인한 유저리스트

    @Builder
    public Feed(String content, String feedType, LocalDateTime createDate,
                Long groupPk, Long writer){
        //this.media = media;
        this.content = content;
        this.feedType = feedType;
        this.groupPk = groupPk;
        this.writer = writer;
        this.createDate = createDate;
        this.updateDate = "0-0-0 0:0:0";
        this.authCheck = "false";
        this.status = "true";
    }

}