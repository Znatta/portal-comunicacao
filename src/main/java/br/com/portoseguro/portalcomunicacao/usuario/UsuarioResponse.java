package br.com.portoseguro.portalcomunicacao.usuario;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo,
        @Schema(allowableValues = {"ADMIN", "PRODUCER"}, description = "Perfil de acesso do usuário")
        String perfil) {

        public UsuarioResponse(Usuario usuario) {
            this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getAtivo(), usuario.getPerfil().name());
        }
}
