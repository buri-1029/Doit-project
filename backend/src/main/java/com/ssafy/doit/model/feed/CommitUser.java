package com.ssafy.doit.model.feed;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "`commit_user`")
public class CommitUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Long userPk;
    private int cnt;

    @Builder
    public CommitUser(LocalDate date, Long userPk, int cnt){
        this.date = date;
        this.userPk = userPk;
        this.cnt = cnt;
    }
}
