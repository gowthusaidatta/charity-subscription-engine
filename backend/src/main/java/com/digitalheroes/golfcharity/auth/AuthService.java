package com.digitalheroes.golfcharity.auth;

import com.digitalheroes.golfcharity.common.BadRequestException;
import com.digitalheroes.golfcharity.enums.Role;
import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import com.digitalheroes.golfcharity.enums.SubscriptionStatus;
import com.digitalheroes.golfcharity.security.JwtService;
import com.digitalheroes.golfcharity.subscription.Subscription;
import com.digitalheroes.golfcharity.subscription.SubscriptionRepository;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            SubscriptionRepository subscriptionRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new BadRequestException("Email already in use");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        Subscription subscription = new Subscription();
        subscription.setUser(savedUser);
        subscription.setPlan(SubscriptionPlan.MONTHLY);
        subscription.setStatus(SubscriptionStatus.INACTIVE);
        subscription.setRenewalDate(LocalDate.now());
        subscriptionRepository.save(subscription);

        String token = jwtService.generateToken(savedUser);
        return new AuthResponse(token, savedUser.getEmail(), savedUser.getRole(), savedUser.getFullName());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email().trim().toLowerCase(), request.password()
        ));

        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole(), user.getFullName());
    }
}
