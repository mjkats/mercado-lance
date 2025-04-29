package br.com.katsilis.mercadolance.exception;

import lombok.Getter;

@Getter
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
