package br.com.katsilis.mercadolance.exception;

public class RedisException extends RuntimeException {

    public RedisException(String message) {
        super(message);
    }
}
