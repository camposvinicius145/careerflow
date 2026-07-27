package com.careerflow.auth;

import com.careerflow.common.exception.ConflictException;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        model.addAttribute("loginError", error != null);
        model.addAttribute("loggedOut", logout != null);
        return "auth/login";
    }

    @GetMapping("/register")
    String registerPage(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm("", "", ""));
        return "auth/register";
    }

    @PostMapping("/register")
    String register(
            @Valid @ModelAttribute RegistrationForm registrationForm,
            BindingResult bindingResult,
            jakarta.servlet.http.HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        UserAccount account;
        try {
            account = authService.register(registrationForm);
        } catch (ConflictException exception) {
            bindingResult.rejectValue("email", "email.exists", exception.getMessage());
            return "auth/register";
        }

        var principal =
                User.withUsername(account.getEmail())
                        .password(account.getPasswordHash())
                        .roles("USER")
                        .build();
        var authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        principal, null, principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        request.getSession(true)
                .setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return "redirect:/dashboard?welcome";
    }
}
