package com.moura.sistemapagamentosbackend.util.exceptions.transaction;

public class TransactionTypeException extends TransactionException{
    public TransactionTypeException() {
        super("usuários lojistas não estão autorizados a realizarem transações");
    }

    public TransactionTypeException(String message) {
        super(message);
    }

    public TransactionTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
