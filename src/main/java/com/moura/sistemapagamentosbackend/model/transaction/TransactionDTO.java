package com.moura.sistemapagamentosbackend.model.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    @NotNull
    private BigDecimal value;

    @NotNull
    private Long payerId;

    @NotNull
    private Long payeeId;
}
