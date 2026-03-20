package com.digitalheroes.golfcharity.config;

import com.digitalheroes.golfcharity.charity.Charity;
import com.digitalheroes.golfcharity.charity.CharityRepository;
import com.digitalheroes.golfcharity.enums.Role;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CharityRepository charityRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:admin@golfcharity.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:Admin@12345}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository, CharityRepository charityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.charityRepository = charityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedCharities();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(adminEmail.toLowerCase())) {
            return;
        }

        User admin = new User();
        admin.setFullName("Platform Admin");
        admin.setEmail(adminEmail.toLowerCase());
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    private void seedCharities() {
        if (charityRepository.count() > 0) {
            return;
        }

        Charity c1 = new Charity();
        c1.setName("Green Hope Trust");
        c1.setSlug("green-hope-trust");
        c1.setDescription("Funding health and education projects for underserved children.");
        c1.setFeatured(true);
        c1.setActive(true);

        Charity c2 = new Charity();
        c2.setName("Birdie Care Foundation");
        c2.setSlug("birdie-care-foundation");
        c2.setDescription("Supporting local community wellness and sports programs.");
        c2.setFeatured(false);
        c2.setActive(true);

        charityRepository.save(c1);
        charityRepository.save(c2);
    }
}
