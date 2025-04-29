package br.com.katsilis.mercadolance.exception.notfound;

import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;

public class TransactionNotFoundException extends GenericNotFoundException {

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
