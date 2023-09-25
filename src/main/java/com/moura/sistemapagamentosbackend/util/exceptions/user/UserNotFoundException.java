package com.moura.sistemapagamentosbackend.util.exceptions.user;

import com.moura.sistemapagamentosbackend.util.exceptions.transaction.TransactionException;

public class UserNotFoundException extends TransactionException {
    public UserNotFoundException() {
        super("usuário não encontrado");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
