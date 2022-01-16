package com.ssafy.doit.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    GUEST("ROLE_GUEST","손님"),
    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "사용자"),
    WITHDRAW("ROLE_WITHDRAW", "탈퇴자");

    private final String key;
    private final String title;
}
