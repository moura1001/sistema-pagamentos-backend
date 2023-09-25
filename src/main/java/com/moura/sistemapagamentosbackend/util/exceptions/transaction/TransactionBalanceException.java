package com.moura.sistemapagamentosbackend.util.exceptions.transaction;

public class TransactionBalanceException extends TransactionException{
    public TransactionBalanceException() {
        super("saldo insuficiente");
    }

    public TransactionBalanceException(String message) {
        super(message);
    }

    public TransactionBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
