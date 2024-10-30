package com.management.restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.management.restaurant.domain.User;
import com.management.restaurant.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/create")
    public String createNewUser() {
        User newUser = new User();
        newUser.setFullName("Ngo Kim Long");
        newUser.setEmail("ngolong.hh@gmail.com");
        newUser.setPassword("123456");
        this.userService.create(newUser);

        return "Create user";
    }
}
