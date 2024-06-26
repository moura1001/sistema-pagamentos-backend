package com.moura.sistemapagamentosbackend.model.user;

import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "name", "document", "email", "type"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String document;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserType type;

    private BigDecimal balance;

    public User(String name, String document, String email, UserType type, BigDecimal balance) {
        this.name = name;
        this.document = document;
        this.email = email;
        this.type = type;
        this.balance = balance;
    }

    public User(UserDTO user) {
        this.name = user.getName();
        this.document = user.getDocument();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.type = user.getType();
        this.balance = user.getBalance();
    }

}
