package br.com.portoseguro.portalcomunicacao.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioAtualizacaoRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotNull Boolean ativo,
        @Schema(allowableValues = {"ADMIN", "PRODUCER"}, description = "Perfil de acesso do usuário")
        @NotBlank String perfil) {
}
