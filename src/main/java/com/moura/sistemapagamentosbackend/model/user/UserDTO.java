package com.moura.sistemapagamentosbackend.model.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private static final String CPF_REGEXP = "^([0-9]{11})$";
    private static final String CNPJ_REGEXP = "^([0-9]{8}(0001|0002)[0-9]{2})$";

    @NotBlank
    @Length(min=3, max=128)
    @Pattern(regexp="^([A-Za-z]+\\s?)+$", message = "nome inválido")
    private String name;

    @NotBlank
    @Length(min=11, max=14)
    @Pattern(regexp=CPF_REGEXP + "|" + CNPJ_REGEXP, message = "documento inválido")
    private String document;

    @NotBlank
    @Length(min=5, max=128)
    @Pattern(regexp="^([\\._]*[A-Za-z0-9]+[\\._]*)@[A-Za-z0-9]+\\.(com|org|br)$", message = "email inválido")
    private String email;

    private String password;

    @NotNull
    private UserType type;

    private BigDecimal balance;

    public UserDTO(String name, String document, String email, UserType type, BigDecimal balance) {
        this.name = name;
        this.document = document;
        this.email = email;
        this.type = type;
        this.balance = balance;
    }

}
