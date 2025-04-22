package br.com.katsilis.mercadolance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping("/public/hello")
    public String publico() {
        return "Acesso público teste";
    }

    @GetMapping
    public String protegido() {
        return "Somente com token válido!";
    }

}
