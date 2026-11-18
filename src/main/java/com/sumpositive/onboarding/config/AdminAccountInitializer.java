

package com.sumpositive.onboarding.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sumpositive.onboarding.entity.User;
import com.sumpositive.onboarding.enums.Role;
import com.sumpositive.onboarding.repository.UserRepository;

@Configuration
public class AdminAccountInitializer {

    @Bean
    @ConditionalOnProperty(
        name = "app.admin.bootstrap-enabled",
        havingValue = "true"
    )
    public CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            org.springframework.core.env.Environment environment) {

        return args -> {

            String email = environment.getProperty("app.admin.email");
            String password = environment.getProperty("app.admin.password");

            if (email == null || email.isBlank()
                    || password == null || password.length() < 12) {
                throw new IllegalStateException(
                    "Admin email and password (minimum 12 characters) must be configured."
                );
            }

            email = email.trim().toLowerCase();

            var existingUser = userRepository.findByMail(email);

            if (existingUser.isPresent()) {

                if (existingUser.get().getRole() != Role.ADMIN) {
                    throw new IllegalStateException(
                        "This email already belongs to a non-admin user. "
                        + "Use a different admin email or provision the role securely."
                    );
                }

                System.out.println("Admin account already exists: " + email);
                return;
            }

            User admin = new User();

            admin.setMail(email);
            admin.setPasswordHash(
                passwordEncoder.encode(password)
            );
            admin.setRole(Role.ADMIN);
            admin.setOnboardingCompleted(false);

            userRepository.save(admin);

            System.out.println("================================");
            System.out.println("ADMIN ACCOUNT CREATED");
            System.out.println("Email: " + email);
            System.out.println("Role: ADMIN");
            System.out.println("================================");
        };
    }
}



