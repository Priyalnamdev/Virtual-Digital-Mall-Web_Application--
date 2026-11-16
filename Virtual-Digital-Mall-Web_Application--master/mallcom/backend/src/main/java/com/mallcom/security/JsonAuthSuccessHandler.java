package com.mallcom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallcom.dto.ApiResponse;
import com.mallcom.dto.UserResponse;
import com.mallcom.entity.User;
import com.mallcom.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * The frontend logs in via fetch() and expects a JSON body back, not an HTTP redirect,
 * so we plug this in instead of Spring Security's default redirect strategy.
 */
@Component
public class JsonAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonAuthSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user vanished from DB"));

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        ApiResponse<UserResponse> body = ApiResponse.ok("Login Successful", UserResponse.from(user));
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
