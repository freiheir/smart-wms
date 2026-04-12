package com.smart.wmserp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WmsErpApplication {
    public static void main(String[] args) {
        SpringApplication.run(WmsErpApplication.class, args);
    }
}
