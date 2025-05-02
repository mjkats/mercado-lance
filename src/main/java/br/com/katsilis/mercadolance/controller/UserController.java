package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.dto.creation.CreateUserDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.dto.update.UpdateUserDto;
import br.com.katsilis.mercadolance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/auth0/{id}")
    public ResponseEntity<UserResponseDto> getByAuth0Id(@PathVariable String id) {
        return ResponseEntity.ok(userService.findByAuth0Id(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserDto user) {
        userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UpdateUserDto user) {
        userService.update(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
