package br.com.katsilis.mercadolance.service;

import br.com.katsilis.mercadolance.dto.creation.CreateUserDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.dto.update.UpdateUserDto;
import br.com.katsilis.mercadolance.model.User;

import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll();
    UserResponseDto findById(Long id);
    UserResponseDto findByAuth0Id(String id);
    User findOriginalById(Long id);
    void create(CreateUserDto user);
    void update(Long id, UpdateUserDto user);
    void deleteById(Long id);
    UserResponseDto userToResponseDto(User user);
}