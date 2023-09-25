package com.moura.sistemapagamentosbackend.util.exceptions.user;

public abstract class UserException extends RuntimeException{
    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
