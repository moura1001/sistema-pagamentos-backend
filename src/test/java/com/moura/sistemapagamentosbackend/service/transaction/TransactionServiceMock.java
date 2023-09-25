package com.moura.sistemapagamentosbackend.service.transaction;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
class TransactionServiceMock {
    @Bean
    @Primary
    public TransactionService mockTransactionService() {
        return Mockito.mock(TransactionService.class, Mockito.CALLS_REAL_METHODS);
    }
}
