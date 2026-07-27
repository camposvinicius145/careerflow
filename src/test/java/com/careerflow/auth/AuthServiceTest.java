package com.careerflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.careerflow.common.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserAccountRepository users;
    @Mock private PasswordEncoder passwordEncoder;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(users, passwordEncoder);
    }

    @Test
    void hashesPasswordBeforePersistingAccount() {
        RegistrationForm form =
                new RegistrationForm("Ana Developer", "ANA@example.com", "secret123");
        when(passwordEncoder.encode("secret123")).thenReturn("bcrypt-hash");
        when(users.save(org.mockito.ArgumentMatchers.any(UserAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount account = service.register(form);

        assertThat(account.getEmail()).isEqualTo("ana@example.com");
        assertThat(account.getPasswordHash()).isEqualTo("bcrypt-hash");
        verify(passwordEncoder).encode("secret123");
    }

    @Test
    void rejectsDuplicatedEmail() {
        RegistrationForm form =
                new RegistrationForm("Ana Developer", "ana@example.com", "secret123");
        when(users.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(form))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Já existe");
    }
}
