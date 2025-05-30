package br.com.katsilis.mercadolance.exception.illegalargument;

import lombok.Getter;

@Getter
public class BidIllegalArgumentException extends IllegalArgumentException {

    private final String userMessage;

    public BidIllegalArgumentException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
