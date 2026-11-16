package com.mallcom.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JsonAuthSuccessHandler authSuccessHandler;
    private final JsonAuthFailureHandler authFailureHandler;
    private final JsonLogoutSuccessHandler logoutSuccessHandler;
    private final JsonAuthEntryPoint authEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JsonAuthSuccessHandler authSuccessHandler,
                           JsonAuthFailureHandler authFailureHandler,
                           JsonLogoutSuccessHandler logoutSuccessHandler,
                           JsonAuthEntryPoint authEntryPoint,
                           JsonAccessDeniedHandler accessDeniedHandler) {
        this.authSuccessHandler = authSuccessHandler;
        this.authFailureHandler = authFailureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // Passwords are always stored/compared with BCrypt, never in plain text.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Required so Spring Security is notified when a session is destroyed,
    // keeping the maximumSessions(1) concurrency check accurate.
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // The frontend is a set of static HTML/JS files served from a different origin
            // (e.g. http://localhost:5500) and talks to this API purely with JSON over fetch();
            // CSRF tokens are not exchanged in that flow, so CSRF protection is disabled here.
            // In a browser-form-based deployment this should be re-enabled with a CSRF-token endpoint.
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                        "/login", "/register",
                        "/api/auth/register", "/api/auth/login",
                        "/css/**", "/js/**", "/images/**"
                ).permitAll()
                // Role-based sections as required by the spec
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/products/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                // Everything else (e.g. /api/auth/me, /api/auth/logout) just needs a login
                .anyRequest().authenticated()
            )

            // ---- Session-based login (no JWT) ----
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login") // Spring Security's filter intercepts this URL
                .usernameParameter("username")          // frontend sends the email as "username"
                .passwordParameter("password")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler)
                .permitAll()
            )

            // ---- Logout ----
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // ---- Session management ----
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
            )

            // ---- 401 / 403 as JSON instead of HTML ----
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            );

        return http.build();
    }

    // Allows the statically-served frontend (different origin/port) to call this API with cookies.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // required so the JSESSIONID cookie is sent/received

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
