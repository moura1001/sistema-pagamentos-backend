package com.moura.sistemapagamentosbackend.model.user;

import lombok.*;

import javax.persistence.*;
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

}
