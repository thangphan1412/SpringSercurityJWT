package org.example.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Helli {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
