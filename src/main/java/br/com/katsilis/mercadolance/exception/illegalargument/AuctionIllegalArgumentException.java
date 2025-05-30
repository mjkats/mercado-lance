package br.com.katsilis.mercadolance.exception.illegalargument;

import lombok.Getter;

@Getter
public class AuctionIllegalArgumentException extends IllegalArgumentException {

    private String userMessage;

    public AuctionIllegalArgumentException(String message, String userMessage) {
        super(message);
        this.userMessage = getUserMessage();
    }
}
