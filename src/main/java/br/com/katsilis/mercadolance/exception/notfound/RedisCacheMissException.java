package br.com.katsilis.mercadolance.exception.notfound;

import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;

public class RedisCacheMissException extends GenericNotFoundException {

    public RedisCacheMissException(String message) {
        super(message);
    }
}
