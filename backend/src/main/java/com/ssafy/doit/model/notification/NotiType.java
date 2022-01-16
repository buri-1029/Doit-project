package com.ssafy.doit.model.notification;


import com.fasterxml.jackson.annotation.JsonValue;

public enum NotiType {
    NEWFEED("0"),
    NEWCHAT("1"),
    CONFIRMFEED("2"),
    KICKOUT("3"),
    AUTHORIZE("4"),
    COMMENT("5");

    private String value;

    NotiType(String value){
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
