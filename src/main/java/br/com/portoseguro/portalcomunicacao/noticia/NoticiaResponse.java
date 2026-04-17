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
        UsuarioResponse autor,
        String imagem) {

    public NoticiaResponse(Noticia noticia, String urlBase) {
        this(
                noticia.getId(),
                noticia.getTitulo(),
                noticia.getSubtitulo(),
                noticia.getConteudo(),
                noticia.getDataPublicacao(),
                noticia.getAtivo(),
                new CategoriaResponse(noticia.getCategoria()),
                new UsuarioResponse(noticia.getAutor()),
                montarUrl(noticia.getImagem(), urlBase)
        );
    }

    // Sobrecarga para manter compatibilidade onde a URL não é necessária (ex: testes simples)
    public NoticiaResponse(Noticia noticia) {
        this(noticia, "");
    }

    private static String montarUrl(String imagem, String urlBase) {
        if (imagem == null || imagem.isBlank() || urlBase == null || urlBase.isBlank()) {
            return imagem;
        }
        // Garante que a URL base termine com barra
        String base = urlBase.endsWith("/") ? urlBase : urlBase + "/";
        return base + imagem;
    }
}
