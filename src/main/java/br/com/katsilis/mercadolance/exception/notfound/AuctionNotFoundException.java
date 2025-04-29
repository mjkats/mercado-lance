package br.com.katsilis.mercadolance.exception.notfound;

import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;

public class AuctionNotFoundException extends GenericNotFoundException {

    public AuctionNotFoundException(String message) {
        super(message);
    }
}
