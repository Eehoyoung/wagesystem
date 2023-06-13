package com.example.wagesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WagesystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(WagesystemApplication.class, args);
    }

}
