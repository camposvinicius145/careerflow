package com.careerflow.auth;

import com.careerflow.common.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserAccountRepository users;

    public CurrentUserService(UserAccountRepository users) {
        this.users = users;
    }

    public CurrentUser require(Authentication authentication) {
        UserAccount user =
                users.findByEmailIgnoreCase(authentication.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        return new CurrentUser(user.getId(), user.getEmail(), user.getDisplayName());
    }
}
