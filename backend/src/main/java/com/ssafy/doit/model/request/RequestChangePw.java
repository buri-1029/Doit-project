package com.ssafy.doit.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RequestChangePw {
    private String email;
    private String password;
    private String authKey;
}
