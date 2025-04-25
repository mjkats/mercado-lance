package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
}
