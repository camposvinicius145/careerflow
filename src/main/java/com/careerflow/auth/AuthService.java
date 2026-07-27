package com.careerflow.auth;

import com.careerflow.common.exception.ConflictException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount register(RegistrationForm form) {
        if (users.existsByEmailIgnoreCase(form.email())) {
            throw new ConflictException("Já existe uma conta com esse e-mail.");
        }
        return users.save(
                UserAccount.create(
                        form.email(), form.displayName(), passwordEncoder.encode(form.password())));
    }
}
