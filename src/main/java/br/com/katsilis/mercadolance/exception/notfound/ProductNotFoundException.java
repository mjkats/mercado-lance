package br.com.katsilis.mercadolance.exception.notfound;

import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;

public class ProductNotFoundException extends GenericNotFoundException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
