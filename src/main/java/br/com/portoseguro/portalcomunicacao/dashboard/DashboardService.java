package br.com.portoseguro.portalcomunicacao.dashboard;

import br.com.portoseguro.portalcomunicacao.categoria.CategoriaRepository;
import br.com.portoseguro.portalcomunicacao.config.SupabaseProperties;
import br.com.portoseguro.portalcomunicacao.noticia.Noticia;
import br.com.portoseguro.portalcomunicacao.noticia.NoticiaRepository;
import br.com.portoseguro.portalcomunicacao.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final NoticiaRepository noticiaRepository;
    private final SupabaseProperties properties;

    private String getPublicUrl() {
        return properties.url() + "/storage/v1/object/public/" + properties.bucket() + "/";
    }

    public DashboardResponse obterMetricas(){
        Long totalNoticias = noticiaRepository.count();
        Long totalCategorias = categoriaRepository.count();
        Long totalUsuarios = usuarioRepository.count();

        List<Noticia> ultimasNoticias = noticiaRepository.findTop5ByAtivoTrueOrderByDataPublicacaoDesc();

        return DashboardResponse.de(totalNoticias, totalCategorias, totalUsuarios, ultimasNoticias, getPublicUrl());
    }
}
