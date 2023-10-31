package com.example.rtlpropagation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JenkinsConfig {

    @Value("${jenkins.url}")
    private String jenkinsUrl;

    @Value("${jenkins.username}")
    private String jenkinsUsername;

    @Value("${jenkins.token}")
    private String jenkinsToken;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getJenkinsUrl() {
        return jenkinsUrl;
    }

    public String getJenkinsUsername() {
        return jenkinsUsername;
    }

    public String getJenkinsToken() {
        return jenkinsToken;
    }
}

