package com.ssafy.doit.model.response;

import com.ssafy.doit.model.Mileage;
import lombok.Getter;
import java.time.format.DateTimeFormatter;

@Getter
public class ResponseMileage {
    String content;
    String date;
    String mileage;

    public ResponseMileage(Mileage mileage){
        this.content = mileage.getContent();
        this.date = mileage.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.mileage = mileage.getMileage();
    }
}
