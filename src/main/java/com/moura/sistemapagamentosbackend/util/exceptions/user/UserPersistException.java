package com.moura.sistemapagamentosbackend.util.exceptions.user;

public class UserPersistException extends UserException {
    public UserPersistException() {
        super("não foi possível salvar os dados solicitados");
    }

    public UserPersistException(String message) {
        super(message);
    }

    public UserPersistException(String message, Throwable cause) {
        super(message, cause);
    }
}
