package com.moura.sistemapagamentosbackend.controller.transaction;

import com.moura.sistemapagamentosbackend.model.transaction.TransactionDTO;
import com.moura.sistemapagamentosbackend.model.user.User;
import com.moura.sistemapagamentosbackend.model.user.UserType;
import com.moura.sistemapagamentosbackend.service.notification.NotificationService;
import com.moura.sistemapagamentosbackend.service.transaction.TransactionService;
import com.moura.sistemapagamentosbackend.service.user.UserService;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionAuthorizeException;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private User user1Common;
    private User user2Common;
    private User user3Merchant;

    @BeforeAll
    void init() {
        List<User> saveUsers = List.of(
                new User("user1", "12345678909", "email1@email.com", UserType.COMMON, new BigDecimal(10)),
                new User("user2", "98765432190", "email2@email.com", UserType.COMMON, new BigDecimal(10)),
                new User("user3", "12345678000109", "email3@email.com", UserType.MERCHANT, new BigDecimal(50))
        );
        userService.saveAllUsers(saveUsers);

        updateUsersRef();
    }

    @BeforeEach
    void setUp() {
        Mockito.doReturn(true).when(transactionService).authorizeTransaction();
        Mockito.doNothing().when(notificationService).sendNotification(Mockito.any(User.class), Mockito.any(String.class));
    }

    @AfterEach
    void tearDown() {
        user1Common.setBalance(new BigDecimal(10));
        user2Common.setBalance(new BigDecimal(10));
        user3Merchant.setBalance(new BigDecimal(50));

        List<User> saveUsers = List.of(
                user1Common,
                user2Common,
                user3Merchant
        );
        userService.saveAllUsers(saveUsers);

        updateUsersRef();

        Mockito.reset(transactionService);
    }

    void updateUsersRef() {
        List<User> getUsers = userService.findAllUsersIn(List.of((long)1, (long)2, (long)3));

        for (User user : getUsers) {
            if (user.getId() == (long) 1) {
                user1Common = user;
            } else if (user.getId() == (long) 2) {
                user2Common = user;
            } else if (user.getId() == (long) 3) {
                user3Merchant = user;
            }
        }
    }

    @Test
    void deveRetornarStatus200ERealizarTransacaoComSucessoEntreUsuariosComuns() {
        try {
            URI uri = new URI("http://localhost:8093/api/v1/transactions");

            TransactionDTO transaction = new TransactionDTO(new BigDecimal(10), user1Common.getId(), user2Common.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TransactionDTO> request = new HttpEntity<>(transaction, headers);

            ResponseEntity<Object> result = testRestTemplate.postForEntity(
                    uri,
                    request,
                    Object.class
            );
            assertThat(HttpStatus.CREATED).isEqualTo(result.getStatusCode());

            updateUsersRef();

            assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(0).setScale(2));
            assertThat(user2Common.getBalance()).isEqualTo(new BigDecimal(20).setScale(2));

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    void deveRetornarStatus500QuandoOServicoDeAutorizacaoEstiverIndisponivel() {
        try {
            TransactionException expected = new TransactionAuthorizeException("serviço indisponível");
            Mockito.doThrow(expected).when(transactionService).createTransaction(Mockito.any(TransactionDTO.class));

            URI uri = new URI("http://localhost:8093/api/v1/transactions");

            TransactionDTO transaction = new TransactionDTO(new BigDecimal(10), user1Common.getId(), user2Common.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TransactionDTO> request = new HttpEntity<>(transaction, headers);

            Map<String, Object> result = testRestTemplate.postForObject(
                    uri,
                    request,
                    Map.class
            );
            assertThat(500).isEqualTo((int) result.get("status"));

            Object aux = result.get("errors");
            assertThat(aux).isNotNull();
            LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) aux;

            List<Object> fieldErrorsList = null;
            LinkedHashMap<String, Object> fieldError = null;

            assertThat(errors.size()).isEqualTo(1);
            assertThat(errors.containsKey("internal")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("internal");
            assertThat(fieldErrorsList.size()).isEqualTo(1);
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldError.get("message")).isEqualTo("serviço indisponível");

            updateUsersRef();

            assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(10).setScale(2));
            assertThat(user2Common.getBalance()).isEqualTo(new BigDecimal(10).setScale(2));

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    void deveRetornarStatus500QuandoOUsuarioDaTransacaoNaoExistir() {
        try {
            URI uri = new URI("http://localhost:8093/api/v1/transactions");

            TransactionDTO transaction = new TransactionDTO(new BigDecimal(10), user1Common.getId(), (long) 99);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TransactionDTO> request = new HttpEntity<>(transaction, headers);

            Map<String, Object> result = testRestTemplate.postForObject(
                    uri,
                    request,
                    Map.class
            );
            assertThat(500).isEqualTo((int) result.get("status"));

            Object aux = result.get("errors");
            assertThat(aux).isNotNull();
            LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) aux;

            List<Object> fieldErrorsList = null;
            LinkedHashMap<String, Object> fieldError = null;

            assertThat(errors.size()).isEqualTo(1);
            assertThat(errors.containsKey("internal")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("internal");
            assertThat(fieldErrorsList.size()).isEqualTo(1);
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldError.get("message")).isEqualTo("usuário não encontrado");

            updateUsersRef();

            assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(10).setScale(2));

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }
}