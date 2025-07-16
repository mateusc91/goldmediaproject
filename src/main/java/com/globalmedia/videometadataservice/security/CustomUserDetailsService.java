package com.globalmedia.videometadataservice.security;

import com.globalmedia.videometadataservice.domain.model.User;
import com.globalmedia.videometadataservice.exception.UsernameCredentialsNotFoundException;
import com.globalmedia.videometadataservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> UsernameCredentialsNotFoundException.forUsername(username));

            var authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );
        } catch (UsernameCredentialsNotFoundException ex) {
            throw new UsernameNotFoundException(ex.getMessage(), ex);
        }
    }
}
