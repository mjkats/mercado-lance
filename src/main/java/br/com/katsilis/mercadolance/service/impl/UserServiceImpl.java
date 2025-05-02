package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateUserDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.dto.update.UpdateUserDto;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.UserNotFoundException;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.UserRepository;
import br.com.katsilis.mercadolance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponseDto> findAll() {
        log.info("Fetching all users");

        List<User> users = userRepository.findAll();
        log.info("Fetched users from database: {}", users);

        return users.stream().map(this::userToResponseDto).toList();
    }

    @Override
    public UserResponseDto findById(Long id) {
        log.info("Fetching user with id {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        log.info("Fetched user from database: {}", user);

        return userToResponseDto(user);
    }

    @Override
    public UserResponseDto findByAuth0Id(String id) {
        log.info("Fetching user with auth0 id {}", id);

        User user = userRepository.findByAuth0Id(id)
            .orElseThrow(() -> new UserNotFoundException("User with auth0 id " + id + " not found"));
        log.info("Fetched user from database: {}", user);

        return userToResponseDto(user);
    }

    @Override
    public User findOriginalById(Long id) {
        log.info("Fetching original user entity with id {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        log.info("Fetched original user entity from database: {}", user);

        return user;
    }

    @Override
    public void create(CreateUserDto user) {
        log.info("Creating new user with data: {}", user);
        LocalDateTime now = LocalDateTime.now();

        User newUser = User.builder()
            .name(user.getName())
            .auth0Id(URLDecoder.decode(user.getAuth0Id(), StandardCharsets.UTF_8))
            .email(user.getEmail())
            .createdAt(now)
            .build();

        try {
            userRepository.save(newUser);
            log.info("User successfully created: {}", newUser);
        } catch (Exception e) {
            throw new DatabaseException("Error while creating user", e);
        }
    }

    @Override
    public void update(Long id, UpdateUserDto user) {
        log.info("Updating user with id {} and new data: {}", id, user);

        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        existingUser.setName(user.getName());

        try {
            userRepository.save(existingUser);
            log.info("User successfully updated: {}", existingUser);
        } catch (Exception e) {
            throw new DatabaseException("Error while updating user with id " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting user with id {}", id);

        try {
            userRepository.deleteById(id);
            log.info("User with id {} successfully deleted", id);
        } catch (Exception e) {
            throw new DatabaseException("Error while deleting user with id " + id, e);
        }
    }

    @Override
    public UserResponseDto userToResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getEmail(), user.getName());
    }
}
