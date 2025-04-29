package br.com.katsilis.mercadolance.exception;

public class JsonParsingException extends RuntimeException {

    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
