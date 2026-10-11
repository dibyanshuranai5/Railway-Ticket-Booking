package com.trainbooking.auth.controller;

import com.trainbooking.auth.dto.LoginRequest;
import com.trainbooking.auth.dto.SessionResponse;
import com.trainbooking.auth.dto.SignUpRequest;
import com.trainbooking.auth.dto.UserProfileResponse;
import com.trainbooking.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SessionResponse> signUp(
            @Valid @RequestBody SignUpRequest input,
            HttpServletRequest request) {
        UserProfileResponse user = authService.signUp(input, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SessionResponse(true, user));
    }

    @PostMapping("/login")
    public SessionResponse logIn(@Valid @RequestBody LoginRequest input, HttpServletRequest request) {
        UserProfileResponse user = authService.logIn(input, request);
        return new SessionResponse(true, user);
    }

    @PostMapping("/logout")
    public MessageResponse logOut(HttpServletRequest request) {
        authService.logOut(request);
        return new MessageResponse("You are signed out.");
    }

    @GetMapping("/session")
    public SessionResponse getSession(HttpServletRequest request, CsrfToken csrfToken) {
        // Resolving the token also writes the XSRF-TOKEN cookie for subsequent form submissions.
        csrfToken.getToken();
        AuthService.SessionState state = authService.session(request);
        return new SessionResponse(state.authenticated(), state.user());
    }

    public record MessageResponse(String message) {
    }
}
