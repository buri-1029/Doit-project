package com.ssafy.doit.model.response;

import com.ssafy.doit.model.user.User;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class ResponseUser {
    private Long userPk;
    private String email;
    private String nickname;
    private int mileage;
    private String role;
    private String createDate;
    private String image;

    public ResponseUser(User user){
        this.userPk = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.mileage = user.getMileage();
        this.role = user.getRoleKey();
        this.image = user.getImage();
        this.createDate = user.getCreateDate().toString();
    }
}
