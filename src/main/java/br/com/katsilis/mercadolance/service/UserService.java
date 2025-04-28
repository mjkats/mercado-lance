package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User save(User user);
    void deleteById(Long id);
}