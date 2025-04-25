package br.com.katsilis.mercadolance.exception.handler;

import lombok.Getter;

@Getter
public class DatabaseException extends RuntimeException {

    private final String action;

    public DatabaseException(String message, String action) {
        super(message);
        this.action = action;
    }
}
