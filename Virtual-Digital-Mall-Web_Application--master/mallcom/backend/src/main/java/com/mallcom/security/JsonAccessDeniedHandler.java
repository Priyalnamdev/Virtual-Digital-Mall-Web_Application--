package com.mallcom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallcom.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Fires when a logged-in USER tries to reach an ADMIN-only URL (e.g. /api/admin/**).
 * Returns a plain 403 JSON "Access Denied" response.
 */
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.fail("Access Denied: you do not have permission to access this resource")));
    }
}
