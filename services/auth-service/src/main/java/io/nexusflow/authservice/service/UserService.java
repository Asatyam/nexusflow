package io.nexusflow.authservice.service;

import io.nexusflow.authservice.dto.AuthResponse;
import io.nexusflow.authservice.entity.User;
import io.nexusflow.authservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(JWTService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public AuthResponse registerUser(String username, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        if (userRepository.findByUsername(username) != null) {
            return new AuthResponse("", "Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);

        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse();
        String token = jwtService.generateToken(username);
        authResponse.setToken(token);
        authResponse.setMessage("User " + username + " registered successfully");
        return authResponse;
    }

    public AuthResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid Username or Password");
        }
        String token =  jwtService.generateToken(username);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setMessage("User " + username + " logged in successfully");
        return authResponse;
    }
}
