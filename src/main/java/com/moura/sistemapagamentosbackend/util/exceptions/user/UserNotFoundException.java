package com.moura.sistemapagamentosbackend.util.exceptions.user;

public class UserNotFoundException extends UserException {
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
