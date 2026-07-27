package com.careerflow.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationForm(
        @NotBlank(message = "Informe seu nome.")
                @Size(max = 120, message = "O nome deve ter até 120 caracteres.")
                String displayName,
        @NotBlank(message = "Informe seu e-mail.")
                @Email(message = "Informe um e-mail válido.")
                @Size(max = 180)
                String email,
        @NotBlank(message = "Crie uma senha.")
                @Size(min = 8, max = 72, message = "Use entre 8 e 72 caracteres.")
                String password) {}
