package com.moura.sistemapagamentosbackend.service.transaction;

import com.moura.sistemapagamentosbackend.service.notification.NotificationService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
class NotificationServiceMock {
    @Bean
    @Primary
    public NotificationService mockNotificationService() {
        return Mockito.mock(NotificationService.class, Mockito.CALLS_REAL_METHODS);
    }
}
