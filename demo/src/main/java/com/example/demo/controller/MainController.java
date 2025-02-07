package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/demo")
    String Hello() {
        return "Hello, This is api_gateway service";
    }
}
