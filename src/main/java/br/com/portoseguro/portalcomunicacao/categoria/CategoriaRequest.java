package br.com.portoseguro.portalcomunicacao.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequest(
        @NotBlank String nome,
        @NotNull Boolean ativo) {
}
