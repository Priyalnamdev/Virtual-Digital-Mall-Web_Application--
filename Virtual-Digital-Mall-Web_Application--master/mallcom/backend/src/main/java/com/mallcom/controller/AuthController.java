package com.mallcom.controller;

import com.mallcom.dto.ApiResponse;
import com.mallcom.dto.RegisterRequest;
import com.mallcom.dto.UserResponse;
import com.mallcom.entity.User;
import com.mallcom.exception.UserNotFoundException;
import com.mallcom.repository.UserRepository;
import com.mallcom.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Registration + "who am I" endpoints.
 *
 * NOTE: There is intentionally no @PostMapping("/login") here. Login is handled
 * entirely by Spring Security's form-login filter, which is configured in
 * SecurityConfig to intercept POST /api/auth/login before it would ever reach
 * a controller. That filter authenticates the user (checking the BCrypt-hashed
 * password) and delegates to JsonAuthSuccessHandler / JsonAuthFailureHandler
 * to produce the JSON response the frontend expects. Logout is likewise handled
 * declaratively by SecurityConfig (POST /api/auth/logout).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse created = userService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Registration Successful", created));
    }

    /**
     * Lets the frontend discover who is currently logged in (and their role)
     * so it can decide which nav links / buttons to show. Requires an active
     * session; unauthenticated calls are rejected with 401 by JsonAuthEntryPoint.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException(authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok("OK", UserResponse.from(user)));
    }
}
