package com.moura.sistemapagamentosbackend.util.exceptions.transaction;

public class TransactionAuthorizeException extends TransactionException{
    public TransactionAuthorizeException() {
        super("transação não autorizada");
    }

    public TransactionAuthorizeException(String message) {
        super(message);
    }

    public TransactionAuthorizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
