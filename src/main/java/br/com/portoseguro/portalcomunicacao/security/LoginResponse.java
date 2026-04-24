package br.com.portoseguro.portalcomunicacao.security;

import br.com.portoseguro.portalcomunicacao.usuario.UsuarioPerfil;

public record LoginResponse(
        String token,
        String nome,
        String email,
        UsuarioPerfil perfil
) {
}
