package com.gestor.chef.gf.infrastructure.config;

import com.gestor.chef.gf.infrastructure.security.JwtAuthenticationFilter;
import com.gestor.chef.gf.infrastructure.security.JwtTokenProvider;
import com.gestor.chef.gf.infrastructure.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/ws/**",
            "/webhook/whatsapp"
    };

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PUBLIC_URLS).permitAll()

                    .requestMatchers("/users/**").hasRole("ADMIN")
                    .requestMatchers("/roles/**").hasRole("ADMIN")
                    .requestMatchers("/reports/**").hasAnyRole("ADMIN", "CONTABLE")
                    .requestMatchers("/inventory/**", "/inventory-movements/**", "/products/**")
                        .hasAnyRole("ADMIN", "COCINA")
                    .requestMatchers("/recipes/**", "/orders/**")
                        .hasAnyRole("ADMIN", "COCINA")
                    .requestMatchers("/suppliers/**").hasAnyRole("ADMIN", "CONTABLE")
                    .requestMatchers("/wastes/**").hasAnyRole("ADMIN", "COCINA", "CONTABLE")
                    .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "COCINA", "CONTABLE")
                    .requestMatchers("/alerts/**").authenticated()
                    .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
