package br.com.portoseguro.portalcomunicacao.dashboard;

import br.com.portoseguro.portalcomunicacao.noticia.Noticia;
import br.com.portoseguro.portalcomunicacao.noticia.NoticiaResponse;

import java.util.List;

public record DashboardResponse(
        Long totalNoticias,
        Long totalCategorias,
        Long totalUsuarios,
        List<NoticiaResponse> ultimasNoticias) {

    public static DashboardResponse de(Long totalNoticias, Long totalCategorias, Long totalUsuarios, List<Noticia> noticias) {
        List<NoticiaResponse> transformadas = (noticias == null) 
            ? List.of() 
            : noticias.stream().map(NoticiaResponse::new).toList();
            
        return new DashboardResponse(totalNoticias, totalCategorias, totalUsuarios, transformadas);
    }
}
