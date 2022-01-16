package com.ssafy.doit.model.feed;

import com.ssafy.doit.model.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`feed_has_user`")
public class FeedUser { // 인증피드에 대해 인증확인한 유저 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_pk")
    @NonNull
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "feed_pk")
    @NonNull
    private Feed feed;
}
