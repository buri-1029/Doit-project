package com.ssafy.doit.model.group;

import com.ssafy.doit.model.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`group_has_user`")
public class GroupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String status; // 가입여부

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_pk")
    @NonNull
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "group_pk")
    @NonNull
    private Group group;
}
