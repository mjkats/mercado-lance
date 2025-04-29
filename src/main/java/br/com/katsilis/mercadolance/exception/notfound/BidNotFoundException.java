package br.com.katsilis.mercadolance.exception.notfound;

import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;

public class BidNotFoundException extends GenericNotFoundException {

    public BidNotFoundException(String message) {
        super(message);
    }
}