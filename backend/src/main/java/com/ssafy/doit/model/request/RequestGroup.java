package com.ssafy.doit.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestGroup {
    //private String image;
    private String name;
    private String content;
    private String category;
    private String endDate;
    private int maxNum;
    private List<String> hashtags;
}
