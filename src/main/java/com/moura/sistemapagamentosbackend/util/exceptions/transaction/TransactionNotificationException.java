package com.moura.sistemapagamentosbackend.util.exceptions.transaction;

public class TransactionNotificationException extends TransactionException{
    public TransactionNotificationException() {
        super("notificação não enviada");
    }

    public TransactionNotificationException(String message) {
        super(message);
    }

    public TransactionNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
