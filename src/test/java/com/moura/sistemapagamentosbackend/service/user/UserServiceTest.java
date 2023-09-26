package com.moura.sistemapagamentosbackend.service.user;

import com.moura.sistemapagamentosbackend.model.user.User;
import com.moura.sistemapagamentosbackend.model.user.UserType;
import com.moura.sistemapagamentosbackend.util.exceptions.user.UserException;
import com.moura.sistemapagamentosbackend.util.exceptions.user.UserPersistException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    @Autowired
    private UserService userService;

    @BeforeAll
    void init() {
        List<User> saveUsers = List.of(
                new User("user1", "12345678909", "email1@email.com", UserType.COMMON, new BigDecimal(10)),
                new User("user2", "98765432190", "email2@email.com", UserType.COMMON, new BigDecimal(10)),
                new User("user3", "12345678000109", "email3@email.com", UserType.MERCHANT, new BigDecimal(50))
        );
        userService.saveAllUsers(saveUsers);
    }

    @Test
    void naoDeveSalvarUsuariosComDocumentosOuEmailsDuplicados() {
        UserException expected = new UserPersistException("CPF/CNPJ e e-mails devem ser únicos no sistema");

        User user = new User("user1", "12345678909", "email1@email.com", UserType.COMMON, new BigDecimal(10));
        RuntimeException actual = assertThrows(UserPersistException.class, () -> userService.saveUser(user));
        assertThat(actual.getMessage()).isEqualTo(expected.getMessage());

        User user2 = new User("user2", "02345678901", "email1@email.com", UserType.MERCHANT, new BigDecimal(10));
        RuntimeException actual2 = assertThrows(UserPersistException.class, () -> userService.saveUser(user2));
        assertThat(actual2.getMessage()).isEqualTo(expected.getMessage());

        assertThat(userService.countUsers()).isEqualTo((long)3);
    }
}