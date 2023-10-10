package com.moura.sistemapagamentosbackend.config;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class AppConfigTest {
    @Bean
    public TestRestTemplate testRestTemplate(){
        return new TestRestTemplate();
    }
}
