package com.ssafy.doit.model.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ResponseBasic {
    public boolean status;
    public String data;
    public Object object;
}
