package br.com.katsilis.mercadolance.exception.notfound;

import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;

public class UserNotFoundException extends GenericNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
