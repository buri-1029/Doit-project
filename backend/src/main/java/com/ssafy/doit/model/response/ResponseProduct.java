package com.ssafy.doit.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssafy.doit.model.store.ProductStatus;

import java.time.LocalDateTime;

public interface ResponseProduct {
    Long getId();
    String getCategory();
    String getTitle();
    String getContent();
    String getImage();
    LocalDateTime getCreateDate();
    int getMileage();
    ProductStatus getStatus();

    default Long getUser_pk(){ return getUserId(); }

    default String getNickname(){
        return getUserNickname();
    }

    default String getEmail(){
        return getUserEmail();
    }

    default String getProfile() { return getUserImage(); }

    @JsonIgnore
    Long getUserId();

    @JsonIgnore
    String getUserNickname();

    @JsonIgnore
    String getUserEmail();

    @JsonIgnore
    String getUserImage();
}
