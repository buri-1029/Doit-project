package com.ssafy.doit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoitApplication.class, args);
    }

}
