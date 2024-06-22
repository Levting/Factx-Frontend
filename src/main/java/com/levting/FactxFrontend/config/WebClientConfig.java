package com.levting.FactxFrontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(){
        //return WebClient.builder().baseUrl("http://localhost:8080").build();
        return WebClient.builder().baseUrl("http://34.74.207.191:8081").build();

    }

}
