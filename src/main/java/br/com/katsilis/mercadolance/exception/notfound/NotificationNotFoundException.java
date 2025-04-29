package br.com.katsilis.mercadolance.exception.notfound;

import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;

public class NotificationNotFoundException extends GenericNotFoundException {

    public NotificationNotFoundException(String message) {
        super(message);
    }
}