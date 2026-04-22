package br.com.portoseguro.portalcomunicacao.newsletter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record NewsletterRequest(
        @NotBlank @Email String email) {
}
