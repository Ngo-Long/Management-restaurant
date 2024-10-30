package com.management.restaurant.service;

import org.springframework.stereotype.Service;

import com.management.restaurant.domain.User;
import com.management.restaurant.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User dataUser) {
        return this.userRepository.save(dataUser);
    }

}
