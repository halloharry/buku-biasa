package com.demo.tes.backend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class DemoApps {
    public static void main(String[] args) {
        SpringApplication.run(DemoApps.class, args);
    }

    // scheduler purpose timezone to WIB
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
    }
}
