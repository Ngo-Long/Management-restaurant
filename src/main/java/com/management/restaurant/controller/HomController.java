package com.management.restaurant.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomController {
    @GetMapping("/")
    public String getHome() {
        return "Hello World";
    }
}
