package br.com.portoseguro.portalcomunicacao.usuario;

public record UsuarioResumoResponse(Long id, String nome) {
    public UsuarioResumoResponse(Usuario usuario) {
        this(usuario.getId(), usuario.getNome());
    }
}
