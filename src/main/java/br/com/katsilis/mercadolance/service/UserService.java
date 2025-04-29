package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateUserDto;
import br.com.katsilis.mercadolance.dto.update.UpdateUserDto;
import br.com.katsilis.mercadolance.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User create(CreateUserDto user);
    void update(Long id, UpdateUserDto user);
    void deleteById(Long id);
}