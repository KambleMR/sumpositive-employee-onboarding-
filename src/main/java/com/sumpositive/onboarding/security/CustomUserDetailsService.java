package com.sumpositive.onboarding.security;



import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sumpositive.onboarding.entity.User;
import com.sumpositive.onboarding.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByMail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "User not found with email: " + email
                    )
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getMail())
                .password(user.getPasswordHash())
                .authorities("ROLE_EMPLOYEE")
                .build();
    }
}
