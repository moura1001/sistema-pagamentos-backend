package com.moura.sistemapagamentosbackend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger3Config {
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("sistemapagamentos-public")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}