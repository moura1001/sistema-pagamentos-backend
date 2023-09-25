package com.moura.sistemapagamentosbackend.model.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private BigDecimal value;

    private Long payerId;

    private Long payeeId;
}
