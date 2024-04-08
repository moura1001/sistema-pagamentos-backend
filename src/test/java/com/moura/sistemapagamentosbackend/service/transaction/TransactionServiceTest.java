package com.moura.sistemapagamentosbackend.service.transaction;

import com.moura.sistemapagamentosbackend.model.transaction.TransactionDTO;
import com.moura.sistemapagamentosbackend.model.user.User;
import com.moura.sistemapagamentosbackend.model.user.UserType;
import com.moura.sistemapagamentosbackend.service.notification.NotificationService;
import com.moura.sistemapagamentosbackend.service.user.UserService;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionAuthorizeException;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionBalanceException;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionException;
import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionTypeException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration
class TransactionServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private NotificationService notificationService;

    private User user1Common;
    private User user2Common;
    private User user3Merchant;

    @BeforeAll
    void init() {
        userService.deleteAllUsers();

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
        List<User> getUsers = userService.getAllUsers();

        for (User user : getUsers) {
            if ("user1".equals(user.getName())) {
                user1Common = user;
            } else if ("user2".equals(user.getName())) {
                user2Common = user;
            } else if ("user3".equals(user.getName())) {
                user3Merchant = user;
            }
        }
    }

    @Test
    void deveRealizarTransacaoComSucessoEntreUsuariosComuns() {
        TransactionDTO transaction = new TransactionDTO(new BigDecimal(10), user1Common.getId(), user2Common.getId());
        assertDoesNotThrow(() -> transactionService.createTransaction(transaction));
        updateUsersRef();

        assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(0).setScale(2));
        assertThat(user2Common.getBalance()).isEqualTo(new BigDecimal(20).setScale(2));
    }

    @Test
    void naoDeveRealizarTransacaoQuandoOServicoDeAutorizacaoEstiverIndisponivel() {
        TransactionException expected = new TransactionAuthorizeException("serviço indisponível");
        Mockito.doThrow(expected).when(transactionService).createTransaction(Mockito.any(TransactionDTO.class));

        TransactionDTO transaction = new TransactionDTO(new BigDecimal(10), user1Common.getId(), user2Common.getId());
        RuntimeException actual = assertThrows(TransactionAuthorizeException.class, () -> transactionService.createTransaction(transaction));
        assertThat(actual.getMessage()).isEqualTo(expected.getMessage());

        updateUsersRef();

        assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(10).setScale(2));
        assertThat(user2Common.getBalance()).isEqualTo(new BigDecimal(10).setScale(2));
    }

    @Test
    void naoDeveRealizarTransacaoQuandoOSaldoEhInsuficiente() {
        TransactionDTO transaction1 = new TransactionDTO(new BigDecimal(5), user1Common.getId(), user2Common.getId());
        assertDoesNotThrow(() -> transactionService.createTransaction(transaction1));
        updateUsersRef();
        assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(5).setScale(2));
        assertThat(user2Common.getBalance()).isEqualTo(new BigDecimal(15).setScale(2));

        TransactionDTO transaction2 = new TransactionDTO(new BigDecimal(10), user1Common.getId(), user2Common.getId());
        assertThrows(TransactionBalanceException.class, () -> transactionService.createTransaction(transaction2));

        updateUsersRef();

        assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(5).setScale(2));
        assertThat(user2Common.getBalance()).isEqualTo(new BigDecimal(15).setScale(2));
    }

    @Test
    void deveRealizarTransacaoComSucessoDeUmUsuarioComumParaUmLojista() {
        TransactionDTO transaction = new TransactionDTO(new BigDecimal(10), user1Common.getId(), user3Merchant.getId());
        assertDoesNotThrow(() -> transactionService.createTransaction(transaction));
        updateUsersRef();

        assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(0).setScale(2));
        assertThat(user3Merchant.getBalance()).isEqualTo(new BigDecimal(60).setScale(2));
    }

    @Test
    void naoDeveRealizarTransferenciasDeUsuariosLojistas() {
        TransactionDTO transaction = new TransactionDTO(new BigDecimal(10), user3Merchant.getId(), user1Common.getId());
        assertThrows(TransactionTypeException.class, () -> transactionService.createTransaction(transaction));

        updateUsersRef();

        assertThat(user1Common.getBalance()).isEqualTo(new BigDecimal(10).setScale(2));
        assertThat(user3Merchant.getBalance()).isEqualTo(new BigDecimal(50).setScale(2));
    }
}