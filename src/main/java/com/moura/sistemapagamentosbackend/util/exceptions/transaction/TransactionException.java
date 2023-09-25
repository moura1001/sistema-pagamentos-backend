package com.moura.sistemapagamentosbackend.util.exceptions.transaction;

public abstract class TransactionException extends RuntimeException{
    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
