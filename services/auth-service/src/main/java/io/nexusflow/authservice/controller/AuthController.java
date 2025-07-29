package io.nexusflow.authservice.controller;

import io.nexusflow.authservice.dto.AuthResponse;
import io.nexusflow.authservice.dto.LoginRequest;
import io.nexusflow.authservice.dto.RegisterRequest;
import io.nexusflow.authservice.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        return userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
    }
}
