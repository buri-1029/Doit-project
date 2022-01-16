package com.ssafy.doit.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.doit.model.group.GroupUser;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pk")
    private Long id;

    private String email;
    private String nickname;
    private String password;
    private LocalDate createDate;
    private String authKey;
    private String image;
    private int mileage;
    private String feedOpen;
    private String groupOpen;
    private String status;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    public List<GroupUser> groupList; // 그룹리스트

    @Builder
    public User(String email, String password, String nickname, LocalDate createDate,
                String image, UserRole userRole, String authKey) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createDate = createDate;
        this.image = image;
        this.userRole = userRole;
        this.authKey = authKey;
        this.mileage = 1000;
        this.feedOpen = "true";
        this.groupOpen = "true";
        this.status = "true";
    }
    public String getRoleKey() {
        return this.userRole.getKey();
    }
}
