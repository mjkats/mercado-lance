package br.com.katsilis.mercadolance.exception.notfound;

import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;

public class PaymentInfoNotFoundException extends GenericNotFoundException {

    public PaymentInfoNotFoundException(String message) {
        super(message);
    }
}