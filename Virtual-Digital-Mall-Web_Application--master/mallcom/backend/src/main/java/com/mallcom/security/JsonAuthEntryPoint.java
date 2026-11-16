package com.mallcom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallcom.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Fires when an unauthenticated request hits a protected URL (e.g. /api/user/**, /api/admin/**).
 * Returns 401 JSON instead of redirecting to an HTML login page.
 */
@Component
public class JsonAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.fail("Authentication required. Please log in.")));
    }
}
