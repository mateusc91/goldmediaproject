package com.globalmedia.videometadataservice.config;

import com.globalmedia.videometadataservice.domain.model.User;
import com.globalmedia.videometadataservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of("ADMIN", "USER"))
                    .build();
            userRepository.save(admin);
            log.info("Admin user created");
        }

        if (!userRepository.existsByUsername("user")) {
            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .roles(Set.of("USER"))
                    .build();
            userRepository.save(user);
            log.info("Regular user created");
        }
    }
}