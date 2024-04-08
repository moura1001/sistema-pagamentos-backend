package com.moura.sistemapagamentosbackend.service.transaction;

import com.moura.sistemapagamentosbackend.model.transaction.Transaction;
import com.moura.sistemapagamentosbackend.model.transaction.TransactionDTO;
import com.moura.sistemapagamentosbackend.model.user.User;
import com.moura.sistemapagamentosbackend.model.user.UserType;
import com.moura.sistemapagamentosbackend.service.notification.NotificationService;
import com.moura.sistemapagamentosbackend.service.user.UserService;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionAuthorizeException;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionBalanceException;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionException;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionTypeException;
import com.moura.sistemapagamentosbackend.util.math.DecimalOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {
    @Value("${app.authorize-transaction-url-service}")
    private String AUTHORIZE_TRANSACTION_URL;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public void createTransaction(TransactionDTO transaction) throws RuntimeException {
        List<User> users = userService.findAllUsersIn(List.of(transaction.getPayerId(), transaction.getPayeeId()));

        User payer = null;
        User payee = null;
        for (User user : users) {
            if (transaction.getPayerId().equals(user.getId())) {
                payer = user;
            } else {
                payee = user;
            }
        }

        validateTransaction(payer, transaction.getValue());

        boolean isAuthorized = authorizeTransaction();
        if (!isAuthorized) {
            throw new TransactionAuthorizeException();
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setPayer(payer);
        newTransaction.setPayee(payee);
        newTransaction.setAmmount(transaction.getValue());
        newTransaction.setTimestamp(LocalDateTime.now());

        payer.setBalance(DecimalOperations.subtract(payer.getBalance(), transaction.getValue()));
        payee.setBalance(DecimalOperations.add(payee.getBalance(), transaction.getValue()));

        repository.save(newTransaction);
        userService.saveAllUsers(users);

        notificationService.sendNotification(payer, "Transação realizada com sucesso");
        notificationService.sendNotification(payee, "Transação realizada com sucesso");
    }

    private void validateTransaction(User payer, BigDecimal ammount) throws TransactionException {
        if (UserType.MERCHANT.equals(payer.getType())) {
            throw new TransactionTypeException();
        }

        if (payer.getBalance().compareTo(ammount) < 0) {
            throw new TransactionBalanceException();
        }
    }

    public boolean authorizeTransaction() throws TransactionException {
        ResponseEntity<Map> response;

        try {
            response = restTemplate.getForEntity(AUTHORIZE_TRANSACTION_URL, Map.class);
        } catch (RuntimeException e) {
            throw new TransactionAuthorizeException("serviço de autorização indisponível: " + e.getMessage());
        }

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            return "Autorizado".equals(response.getBody().get("message"));
        } else {
            return  false;
        }
    }
}
