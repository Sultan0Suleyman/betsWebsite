package com.sobolbetbackend.backendprojektbk1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@SpringBootApplication
@EnableScheduling
public class BackendProjektBk1Application {
    public static void main(String[] args) {
        SpringApplication.run(BackendProjektBk1Application.class, args);
    }

}
