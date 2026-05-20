package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.User;
import com.gestor.chef.gf.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("user-1")
                .name("Luisa Chef")
                .email("luisa@gestor.chef")
                .password("RawPass123!")
                .rol("ADMIN")
                .accountStatus("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("crea usuario y cifra contraseña")
    void createUserSuccess() {
        when(userRepository.existsByEmail("luisa@gestor.chef")).thenReturn(false);
        when(passwordEncoder.encode("RawPass123!")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User created = userService.createUser(User.builder()
                .name("Luisa Chef")
                .email("luisa@gestor.chef")
                .password("RawPass123!")
                .rol("ADMIN")
                .build());
        assertThat(created.getPassword()).isEqualTo("$2a$hashed");
        assertThat(created.getAccountStatus()).isEqualTo("ACTIVE");
        assertThat(created.getCreatedAt()).isNotNull();
        verify(passwordEncoder).encode("RawPass123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("rechaza email duplicado")
    void createUserDuplicateEmailThrows() {
        when(userRepository.existsByEmail("luisa@gestor.chef")).thenReturn(true);
        User input = User.builder().email("luisa@gestor.chef").password("RawPass123!").rol("ADMIN").build();
        assertThatThrownBy(() -> userService.createUser(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya está registrado");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("actualiza campos no nulos")
    void updateUserUpdatesOnlyNonNullFields() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User updated = userService.updateUser("user-1", User.builder().name("Luisa Admin").build());
        assertThat(updated.getName()).isEqualTo("Luisa Admin");
        assertThat(updated.getEmail()).isEqualTo("luisa@gestor.chef");
    }

    @Test
    @DisplayName("obtiene usuario por id")
    void getUserByIdFound() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
        assertThat(userService.getUserById("user-1")).isPresent();
    }

    @Test
    @DisplayName("lista usuarios activos")
    void getAllUsersReturnsActiveList() {
        when(userRepository.findByAccountStatus("ACTIVE")).thenReturn(List.of(sampleUser));
        List<User> users = userService.getAllUsers();
        assertThat(users).hasSize(1);
    }

    @Test
    @DisplayName("cambia estado")
    void changeAccountStatusSuccess() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User result = userService.changeAccountStatus("user-1", "INACTIVE");
        assertThat(result.getAccountStatus()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("inactiva usuario")
    void deleteUserInactivates() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        userService.deleteUser("user-1");
        verify(userRepository).save(argThat(user -> "INACTIVE".equals(user.getAccountStatus())));
    }

    @Test
    @DisplayName("cambia contraseña")
    void changePasswordEncodesAndSaves() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("NuevaClave456!")).thenReturn("$2a$newHash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User result = userService.changePassword("user-1", "NuevaClave456!");
        assertThat(result.getPassword()).isEqualTo("$2a$newHash");
        verify(passwordEncoder).encode("NuevaClave456!");
    }
}
