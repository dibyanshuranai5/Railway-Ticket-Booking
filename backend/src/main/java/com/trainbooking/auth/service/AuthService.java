package com.trainbooking.auth.service;

import com.trainbooking.auth.dto.LoginRequest;
import com.trainbooking.auth.dto.ProfileUpdateRequest;
import com.trainbooking.auth.dto.SignUpRequest;
import com.trainbooking.auth.dto.UserProfileResponse;
import com.trainbooking.auth.model.User;
import com.trainbooking.auth.repository.UserRepository;
import com.trainbooking.config.MongoIndexInitializer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String USER_ID_SESSION_KEY = "trainbooking.userId";

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final MongoIndexInitializer indexInitializer;

    public AuthService(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            MongoIndexInitializer indexInitializer) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.indexInitializer = indexInitializer;
    }

    public UserProfileResponse signUp(SignUpRequest input, HttpServletRequest request) {
        indexInitializer.ensureIndexes();
        String email = normalizeEmail(input.email());
        if (users.existsByEmail(email)) {
            throw new AuthenticationException("ACCOUNT_EXISTS", "An account with this email already exists.", HttpStatus.BAD_REQUEST);
        }

        User user = users.save(new User(
                input.name().trim(),
                email,
                input.phone().trim(),
                passwordEncoder.encode(input.password())));
        establishSession(request, user.id);
        return toProfile(user);
    }

    public UserProfileResponse logIn(LoginRequest input, HttpServletRequest request) {
        User user = users.findByEmail(normalizeEmail(input.email()))
                .orElseThrow(() -> invalidCredentials());
        if (!passwordEncoder.matches(input.password(), user.passwordHash)) {
            throw invalidCredentials();
        }
        establishSession(request, user.id);
        return toProfile(user);
    }

    public UserProfileResponse requireProfile(HttpServletRequest request) {
        String userId = requireUserId(request);
        return users.findById(userId)
                .map(AuthService::toProfile)
                .orElseThrow(() -> new AuthenticationException(
                        "UNAUTHENTICATED", "Please sign in again.", HttpStatus.UNAUTHORIZED));
    }

    public User requireUser(HttpServletRequest request) {
        String userId = requireUserId(request);
        return users.findById(userId)
                .orElseThrow(() -> new AuthenticationException(
                        "UNAUTHENTICATED", "Please sign in again.", HttpStatus.UNAUTHORIZED));
    }

    public UserProfileResponse updateProfile(HttpServletRequest request, ProfileUpdateRequest input) {
        User user = requireUser(request);
        user.name = input.name().trim();
        user.phone = input.phone().trim();
        return toProfile(users.save(user));
    }

    public SessionState session(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return new SessionState(false, null);
        }
        Object rawUserId = session.getAttribute(USER_ID_SESSION_KEY);
        if (!(rawUserId instanceof String userId)) {
            return new SessionState(false, null);
        }
        return users.findById(userId)
                .map(user -> new SessionState(true, toProfile(user)))
                .orElseGet(() -> new SessionState(false, null));
    }

    public void logOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private String requireUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object userId = session == null ? null : session.getAttribute(USER_ID_SESSION_KEY);
        if (!(userId instanceof String value) || value.isBlank()) {
            throw new AuthenticationException("UNAUTHENTICATED", "Please sign in to continue.", HttpStatus.UNAUTHORIZED);
        }
        return value;
    }

    private void establishSession(HttpServletRequest request, String userId) {
        HttpSession previous = request.getSession(false);
        if (previous != null) {
            previous.invalidate();
        }
        request.getSession(true).setAttribute(USER_ID_SESSION_KEY, userId);
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private static AuthenticationException invalidCredentials() {
        return new AuthenticationException(
                "INVALID_CREDENTIALS", "Email or password is incorrect.", HttpStatus.UNAUTHORIZED);
    }

    private static UserProfileResponse toProfile(User user) {
        return new UserProfileResponse(user.id, user.name, user.email, user.phone);
    }

    public record SessionState(boolean authenticated, UserProfileResponse user) {
    }
}
