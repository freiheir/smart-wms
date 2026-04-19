package com.smart.wmserp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                    "http://localhost:*", "https://localhost:*",
                    "http://127.0.0.1:*", "https://127.0.0.1:*",
                    "http://172.*.*.*:*", "https://172.*.*.*:*",
                    "http://192.168.*.*:*", "https://192.168.*.*:*",
                    "http://10.*.*.*:*", "https://10.*.*.*:*",
                    "http://64.110.100.247:*", "https://64.110.100.247:*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
