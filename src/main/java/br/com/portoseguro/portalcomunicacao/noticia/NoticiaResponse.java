package br.com.portoseguro.portalcomunicacao.noticia;

import br.com.portoseguro.portalcomunicacao.categoria.CategoriaResponse;
import br.com.portoseguro.portalcomunicacao.usuario.UsuarioResponse;

import java.time.LocalDateTime;

public record NoticiaResponse(
        Long id,
        String titulo,
        String subtitulo,
        String conteudo,
        LocalDateTime dataPublicacao,
        Boolean ativo,
        CategoriaResponse categoria,
        UsuarioResponse autor) {
}
