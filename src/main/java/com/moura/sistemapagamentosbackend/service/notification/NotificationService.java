package com.moura.sistemapagamentosbackend.service.notification;

import com.moura.sistemapagamentosbackend.model.notification.NotificationDTO;
import com.moura.sistemapagamentosbackend.model.user.User;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionNotificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Value("${app.notification-transaction-url-service}")
    private String NOTIFICATION_TRANSACTION_URL;

    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) throws RuntimeException {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        ResponseEntity<Object> response;

        try {
            response = restTemplate.postForEntity(NOTIFICATION_TRANSACTION_URL, notificationRequest, Object.class);
        } catch (RuntimeException e) {
            throw new TransactionNotificationException("serviço de notificação indisponível: " + e.getMessage());
        }

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new TransactionNotificationException("serviço de notificação está fora do ar");
        }
    }
}
