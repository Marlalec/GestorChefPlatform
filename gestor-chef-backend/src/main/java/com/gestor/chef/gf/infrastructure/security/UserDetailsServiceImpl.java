package com.gestor.chef.gf.infrastructure.security;

import com.gestor.chef.gf.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepositoryPort userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .filter(u -> "ACTIVE".equals(u.getAccountStatus()))
                .map(u -> User.builder()
                        .username(u.getEmail())
                        .password(u.getPassword())
                        .authorities(Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + u.getRol())))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
