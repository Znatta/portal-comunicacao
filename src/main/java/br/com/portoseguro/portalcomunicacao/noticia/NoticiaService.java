package br.com.portoseguro.portalcomunicacao.noticia;

import br.com.portoseguro.portalcomunicacao.categoria.CategoriaRepository;
import br.com.portoseguro.portalcomunicacao.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public NoticiaResponse criar(NoticiaRequest request) {
        Noticia noticia = new Noticia();

        noticia.setTitulo(request.titulo());
        noticia.setSubtitulo(request.subtitulo());
        noticia.setConteudo(request.conteudo());
        noticia.setDataPublicacao(request.dataPublicacao());
        noticia.setAtivo(request.ativo());
        noticia.setCategoria(categoriaRepository.getReferenceById(request.categoriaId()));
        noticia.setAutor(usuarioRepository.getReferenceById(request.autorId()));

        Noticia noticiaSalva = noticiaRepository.save(noticia);

        return new NoticiaResponse(noticiaSalva);
    }

    public Page<NoticiaResponse> listar(String busca, Long categoriaId, Boolean ativo, Pageable pageable){
        Specification<Noticia> spec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        if(busca != null && !busca.isBlank()){
            spec = spec.and((root, query, criteriaBuilder) -> {
                String padraoBusca = "%" + busca.toLowerCase() + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), padraoBusca),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("subtitulo")), padraoBusca),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("conteudo")), padraoBusca)
                );
            });
        }

        if (categoriaId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("categoria").get("id"), categoriaId)
            );
        }

        if(ativo != null){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ativo"), ativo));
        }

        Page<Noticia> noticias = noticiaRepository.findAll(spec, pageable);

        return  noticias.map(noticia -> new NoticiaResponse(noticia));
    }

    public NoticiaResponse buscarPorId(Long id, Boolean ativo) {
        return noticiaRepository.findById(id)
                .filter(noticia -> ativo == null || noticia.getAtivo().equals(ativo))
                .map(noticia -> new NoticiaResponse(noticia))
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }

    @Transactional
    public NoticiaResponse atualizar(Long id, NoticiaRequest request) {
        return noticiaRepository.findById(id)
                .map(noticia -> {
                    noticia.setTitulo(request.titulo());
                    noticia.setSubtitulo(request.subtitulo());
                    noticia.setConteudo(request.conteudo());
                    noticia.setDataPublicacao(request.dataPublicacao());
                    noticia.setAtivo(request.ativo());
                    noticia.setCategoria(categoriaRepository.getReferenceById(request.categoriaId()));
                    noticia.setAutor(usuarioRepository.getReferenceById(request.autorId()));

                    Noticia noticiaSalva = noticiaRepository.save(noticia);

                    return new NoticiaResponse(noticiaSalva);
                })
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }

    @Transactional
    public NoticiaResponse inativar(Long id) {
        return noticiaRepository.findById(id)
                .map(noticia -> {

                    noticia.setAtivo(false);

                    Noticia noticiaAtualizada = noticiaRepository.save(noticia);

                    return new NoticiaResponse(noticiaAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }

    @Transactional
    public NoticiaResponse ativar(Long id) {
        return noticiaRepository.findById(id)
                .map(noticia -> {

                    noticia.setAtivo(true);

                    Noticia noticiaAtualizada = noticiaRepository.save(noticia);

                    return new NoticiaResponse(noticiaAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Notícia não encontrada com o ID: " + id));
    }
}
