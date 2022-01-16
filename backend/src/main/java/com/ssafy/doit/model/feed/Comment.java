package com.ssafy.doit.model.feed;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "`comment`")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentPk;

    private String content;

    private Long feedPk;
    private Long userPk;

    private LocalDateTime createDate;
    private String updateDate;

    @Builder
    public Comment(String content, LocalDateTime createDate,
                Long feedPk, Long userPk){
        this.content = content;
        this.feedPk = feedPk;
        this.userPk = userPk;
        this.createDate = createDate;

        this.updateDate = "0-0-0 0:0:0";
    }
}
