package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreateUserDto;
import br.com.katsilis.mercadolance.dto.update.UpdateUserDto;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.UserRepository;
import br.com.katsilis.mercadolance.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public User create(CreateUserDto user) {
        LocalDateTime now = LocalDateTime.now();

        User newUser = User
            .builder()
            .name(user.getName())
            .auth0Id(user.getAuth0Id())
            .email(user.getEmail())
            .createdAt(now)
            .build();

        return userRepository.save(newUser);
    }

    @Override
    public void update(Long id, UpdateUserDto user) {
        User existingUser = findById(id);

        if (existingUser == null)
            throw new EntityNotFoundException("User with id " + id + " not found");

        existingUser.setName(user.getName());
        userRepository.save(existingUser);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}