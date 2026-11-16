package com.mallcom.service;

import com.mallcom.dto.RegisterRequest;
import com.mallcom.dto.UserResponse;
import com.mallcom.entity.Role;
import com.mallcom.entity.User;
import com.mallcom.exception.DuplicateEmailException;
import com.mallcom.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Business logic for user registration.
 * Login itself is handled by Spring Security's form-login filter
 * (see SecurityConfig) rather than here, since we use session-based auth.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection (preferred over @Autowired field injection)
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new self-service account.
     * Every account created through this path is forced to Role.USER —
     * ADMIN accounts are only ever created by hand, directly in the database.
     */
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()), // never store plain text passwords
                Role.USER
        );

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }
}
