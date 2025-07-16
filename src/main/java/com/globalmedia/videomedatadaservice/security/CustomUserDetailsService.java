package com.globalmedia.videomedatadaservice.security;

import com.globalmedia.videomedatadaservice.domain.model.User;
import com.globalmedia.videomedatadaservice.exception.UsernameCredentialsNotFoundException;
import com.globalmedia.videomedatadaservice.repository.UserRepository;
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
